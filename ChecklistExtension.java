global with sharing class ChecklistExtension {

    public ChecklistExtension(ApexPages.StandardController stdController) {}

    /** Returns the JSON form of all checklists. */
    public static String getChecklists() {
       return JSON.serialize(ChecklistUtilities.getAllChecklists());
    } 

    /** Returns all pending Checklists. */
    @RemoteAction
    global static List<Checklist_Response__c> pendingChecklists() {
        if (!Schema.SObjectType.Checklist_Response__c.isAccessible()) {
            return null;
        }
        List<Checklist_Response__c> checklists = [SELECT Id, Checklist__r.Name, Checklist__r.Description__c, 
                                                Checklist__r.Id FROM Checklist_Response__c WHERE Status__c=:'Pending' AND Responder__c=:UserInfo.getUserId()];
       return checklists;
    } 

    /** Returns all completed Checklists. */
    @RemoteAction
    global static List<Checklist_Response__c> completedChecklists() {
        if (!Schema.SObjectType.Checklist_Response__c.isAccessible()) {
            return null;
        }
        List<Checklist_Response__c> checklists = [SELECT Id, Checklist__r.Name, Checklist__r.Description__c, 
                                                Checklist__r.Id, Location__latitude__s, Location__longitude__s, LastModifiedDate FROM Checklist_Response__c WHERE Status__c=:'Complete' AND Responder__c=:UserInfo.getUserId()];
        return checklists;
    } 

    /** Returns Checklist Responses to a CHECKLIST. */
    @RemoteAction
    global static List<Checklist_Item_Response__c> getAllChecklistItems(Id checklist){
        Boolean valid = Schema.SObjectType.Checklist_Item__c.isAccessible() && Schema.SObjectType.Checklist_Item_Response__c.isCreateable();
        if (!valid) {
            return null;
        }
        List<Checklist_Item__c> tempList = [SELECT Id, Order__c, Question__c, Required__c, Type__c, Checklist__c, Values__c, Attach_Photo__c 
                FROM Checklist_Item__c WHERE Checklist__c=:checklist AND isActive__c = True order by Order__c];

        List<Checklist_Item_Response__c> responses = new List<Checklist_Item_Response__c>();
        
        for (Checklist_Item__c item : tempList){
            Checklist_Item_Response__c resp = new Checklist_Item_Response__c(Checklist_Item__c = item.Id, 
                                                                             Checklist_Item__r = item);
            responses.add(resp);            
        }   
        return responses;
    }

    /** Updates the RESPONSES to CHECKLISTRESPID and returns the Checklist Response. */
    public static Checklist_Response__c responseUpdate(String checklistRespId, List<Checklist_Item_Response__c> responses) {
        Boolean valid = (Schema.SObjectType.Checklist_Response__c.isAccessible()
            || Schema.SObjectType.Checklist_Item_Response__c.isCreateable())
            && (Schema.SObjectType.Checklist_Item_Response__c.isUpdateable()
            || Schema.SObjectType.Checklist_Item_Response__c.isCreateable());
        if (!valid) {
            return null;
        }
        List<Checklist_Response__c> responseDB = [SELECT Id FROM Checklist_Response__c WHERE Id=:checklistRespId];
        Checklist_Response__c  response;
        Boolean newResponse = false;
        if (!(checklistRespId != null && checklistRespId != '' && responseDB.size()>0)) {
            response = new Checklist_Response__c();
            response.Checklist__c = responses[0].Checklist_Item__r.Checklist__c;
            insert(response);
            newResponse = true;
        } else {
            response = responseDB[0];
        }
        List<Checklist_Item_Response__c> finalResponses = new List<Checklist_Item_Response__c>();
        for(Checklist_Item_Response__c resp : responses){
            if (resp.Answer__c != null || resp.Checklist_Item__r.Attach_Photo__c){
                if (newResponse) {
                        resp.Checklist_Response__c = response.Id;
                }
                    resp.Answer__c = String.valueOf(resp.Answer__c);
                    finalResponses.add(resp);
                 }
            }
            upsert finalResponses;
            return response;
    }
    
    /** Saves a pending Checklist Response whose ID is CHECKLISTRESPONSEID containing RESPONSES. */
    @RemoteAction
    global static Id saveResponses(String checklistRespId,
        List<Checklist_Item_Response__c> responses) {
        if (responses == null || responses.size() == 0
            || !Schema.SObjectType.Checklist_Response__c.isUpdateable()) {
            return null;
        }
        Checklist_Response__c r = responseUpdate(checklistRespId, responses);
        r.Status__c = 'Pending';
        r.Responder__c = UserInfo.getUserId();
        update r;
        return r.Id;
    }

    /** Completes a Checklist Response whose ID is CHECCKLISTRESPID containing RESPONSES. */
    @RemoteAction
    global static Id submitResponses(String checklistRespId, List<Checklist_Item_Response__c> responses, Double latitude, Double longitude) {
        if (responses == null || responses.size() == 0 || !Schema.SObjectType.Checklist_Response__c.isUpdateable())
            return null;
        Checklist_Response__c r = responseUpdate(checklistRespId, responses);
        r.Status__c = 'Complete';
        if (latitude != 0 || longitude != 0) {
            r.Location__latitude__s = latitude;
            r.Location__longitude__s = longitude;
        }
        r.Responder__c = UserInfo.getUserId();
        update r;
        return r.Id;
    }


    /** Returns Checklist Item Responses to edit for CHECKLISTRESPONSE. */
    @RemoteAction
    global static List<Checklist_Item_Response__c> editChecklistItems(Id checklistResponse) {        
        if(!Schema.SObjectType.Checklist_Item_Response__c.isAccessible()) {
            return null;
        }
        List<Checklist_Item_Response__c> tempList = [SELECT Id, Answer__c, Checklist_Item__c, Checklist_Item__r.Order__c, Checklist_Item__r.Question__c, Checklist_Item__r.Checklist__c,
                                                      Checklist_Item__r.Required__c, Checklist_Item__r.Type__c, Checklist_Item__r.Values__c, Checklist_Item__r.Attach_Photo__c
                                                      FROM Checklist_Item_Response__c WHERE Checklist_Response__c=:checklistResponse 
                                                      order by Checklist_Item__r.Order__c];

        Map<Id, Checklist_Item_Response__c> checklistItemId2Resp = new Map<Id, Checklist_Item_Response__c>();
        for (Checklist_Item_Response__c r : tempList){
            checklistItemId2Resp.put(r.Checklist_Item__c, r);
        }
        Id checklistId;
        if (tempList.size() == 0) {
            Checklist_Response__c[] resp = [select Checklist__c from Checklist_Response__c where id = :checklistResponse limit 1];
            if (resp.size() == 1) {
                checklistId = resp[0].Checklist__c;
            }
        } else {
            checklistId = tempList[0].Checklist_Item__r.Checklist__c;
        }

        List<Checklist_Item_Response__c> responses = new List<Checklist_Item_Response__c>();
        
        for (Checklist_Item__c item : [  SELECT Id, Order__c, Question__c, Required__c, Type__c, Checklist__c, Values__c, Attach_Photo__c 
                                          FROM Checklist_Item__c WHERE Checklist__c=:checklistId 
                                          AND isActive__c = True order by Order__c ]) {
            Checklist_Item_Response__c r = checklistItemId2Resp.get(item.Id);
            if (r == null) {
                Checklist_Item_Response__c emptyResp = new Checklist_Item_Response__c(Checklist_Item__c = item.Id, 
                  Checklist_Item__r = item, Checklist_Response__c = checklistResponse);
                responses.add(emptyResp);                                                                               
            } else {
                responses.add(r);
            }
        }
        return responses;
    }

}