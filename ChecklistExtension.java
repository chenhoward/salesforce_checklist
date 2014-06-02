global with sharing class ChecklistExtension {

    public ChecklistExtension(ApexPages.StandardController stdController) {}

    /** Returns the JSON form of all checklists. */
    public static String getChecklists() {
       return JSON.serialize(ChecklistUtilities.getAllChecklists());
    } 

    /** Returns all pending Checklists. */
    @RemoteAction
    global static List<Checklist_Response__c> pending_checklists() {
       List<Checklist_Response__c> checklists = [SELECT Id, Checklist__r.Name, Checklist__r.Description__c, 
                                                Checklist__r.Id FROM Checklist_Response__c WHERE Status__c=:'Pending'];
       return checklists;
    } 

    /** Returns all completed Checklists. */
    @RemoteAction
    global static List<Checklist_Response__c> completed_checklists() {
       List<Checklist_Response__c> checklists = [SELECT Id, Checklist__r.Name, Checklist__r.Description__c, 
                                                Checklist__r.Id FROM Checklist_Response__c WHERE Status__c=:'Complete'];
       return checklists;
    } 

    /** Returns Checklist Responses to a CHECKLIST. */
    public static List<Checklist_Item_Response__c> getAllChecklistItems(Id checklist){
        List<Checklist_Item__c> to_return = [SELECT Id, Order__c, Question__c, Required__c, Type__c, Checklist__c, Values__c, Attach_Photo__c 
                FROM Checklist_Item__c WHERE Checklist__c=:checklist AND isActive__c = True order by Order__c];

        List<Checklist_Item_Response__c> responses = new List<Checklist_Item_Response__c>();
        
        for (Checklist_Item__c item : to_return){
            Checklist_Item_Response__c resp = new Checklist_Item_Response__c(Checklist_Item__c = item.Id, 
                                                                             Checklist_Item__r = item);
            responses.add(resp);            
        }   
        return responses;
    }

    /** Updates the RESPONSES to CHECKLISTRESPID and returns the response. */
    public static Checklist_Response__c save_helper(String checklistRespId, List<Checklist_Item_Response__c> responses) {
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
            if (resp.Answer__c != null){
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
    global static Id saveResponses(String checklistRespId, List<Checklist_Item_Response__c> responses) {
        if (responses == null || responses.size() == 0)
            return null;
        Checklist_Response__c r = save_helper(checklistRespId, responses);
        r.Status__c = 'Pending';
        update r;
        return r.Id;
    }

    /** Completes a Checklist Response whose ID is CHECCKLISTRESPID containing RESPONSES. */
    @RemoteAction
    global static Id submitResponses(String checklistRespId, List<Checklist_Item_Response__c> responses) {
        if (responses == null || responses.size() == 0)
            return null;
        Checklist_Response__c r = save_helper(checklistRespId, responses);
        r.Status__c = 'Complete';
        update r;
        return r.Id;
    }

    // Creates Checklist Response Item Objects
    @RemoteAction
    global static List<Checklist_Item_Response__c> checklist_items(Id checklist) {
        return getAllChecklistItems(checklist);
    }

    @RemoteAction
    global static List<Checklist_Item_Response__c> edit_checklist_items(Id checklist_response) {
        List<Checklist_Item_Response__c> to_return = [SELECT Id, Answer__c, Checklist_Item__c, Checklist_Item__r.Order__c, Checklist_Item__r.Question__c, Checklist_Item__r.Checklist__c,
                                                      Checklist_Item__r.Required__c, Checklist_Item__r.Type__c, Checklist_Item__r.Values__c, Checklist_Item__r.Attach_Photo__c
                                                      FROM Checklist_Item_Response__c WHERE Checklist_Response__c=:checklist_response 
                                                      order by Checklist_Item__r.Order__c];

        Map<Id, Checklist_Item_Response__c> checklistItemId2Resp = new Map<Id, Checklist_Item_Response__c>();
        for (Checklist_Item_Response__c r : to_return){
            checklistItemId2Resp.put(r.Checklist_Item__c, r);
        }
        Id checklistId;
        if (to_return.size() == 0) {
            Checklist_Response__c[] resp = [select Checklist__c from Checklist_Response__c where id = :checklist_response limit 1];
            if (resp.size() == 1) {
                checklistId = resp[0].Checklist__c;
            }
        } else {
            checklistId = to_return[0].Checklist_Item__r.Checklist__c;
        }

        List<Checklist_Item_Response__c> responses = new List<Checklist_Item_Response__c>();
        
        for (Checklist_Item__c item : [  SELECT Id, Order__c, Question__c, Required__c, Type__c, Checklist__c, Values__c, Attach_Photo__c 
                                          FROM Checklist_Item__c WHERE Checklist__c=:checklistId 
                                          AND isActive__c = True order by Order__c ]) {
            Checklist_Item_Response__c r = checklistItemId2Resp.get(item.Id);
            if (r == null) {
                Checklist_Item_Response__c emptyResp = new Checklist_Item_Response__c(Checklist_Item__c = item.Id, 
                                                                                        Checklist_Item__r = item,
                                                                                       Checklist_Response__c = checklist_response);
                responses.add(emptyResp);                                                                               
            } else {
                responses.add(r);
            }

        }
        return responses;
    }

    @RemoteAction
    global static String lat_long(Id checklist_response) {
        Checklist_Response__c response = [SELECT Location__c FROM Checklist_Response__c WHERE Id=: checklist_response];
        return JSON.serialize(response);
    }

    @RemoteAction
    global static String[] finish_checklist_items(Id checklist_response_id) {
       Checklist_Response__c r = [SELECT Id FROM Checklist_Response__c WHERE Id=:checklist_response_id];
       r.Status__c = 'Complete';
       update(r);
       List<Checklist_Response__c> pending_checklists = [SELECT Id, Checklist__r.Name, Checklist__r.Description__c, 
                                                         Checklist__r.Id FROM Checklist_Response__c WHERE Status__c=:'Pending'];
       List<Checklist_Response__c> completed_checklists = [SELECT Id, Checklist__r.Name, Checklist__r.Description__c, 
                                                          Checklist__r.Id FROM Checklist_Response__c WHERE Status__c=:'Complete'];
       String return_pending = JSON.serialize(pending_checklists);
       String return_completed = JSON.serialize(completed_checklists);       
       String[] to_return = new String[]{};
       to_return[0] = return_pending;
       to_return[1] = return_completed;
       return to_return;
    }

    @RemoteAction
    global static Id photo_remotecall(Id checklist_item_id, Id response_id, Blob bitphoto) {
        if (checklist_item_id == null || response_id == null || bitphoto == null) {
            return null;
        }
        Checklist_Item_Response__c resp = [SELECT Id FROM Checklist_Item_Response__c WHERE Checklist_Item__c=:checklist_item_id AND Checklist_Response__c=:response_id];
        List<Attachment> a = [SELECT Id FROM Attachment WHERE ParentId=:resp.Id];
        if (a.size() != 0) {
            for (Integer i=0; i<a.size(); i++) {
                delete a[i];
            }
        }
        Attachment attach = new Attachment();
        attach.Body = bitphoto;
        attach.Name = 'Photo for response: ' + resp.Id;
        // attach.ContentType = ;
        attach.ParentID = resp.Id;
        insert attach;
        return attach.Id;
    }

}