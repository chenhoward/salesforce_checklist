global with sharing class ChecklistExtension {

    public ChecklistExtension(ApexPages.StandardController stdController) {}

    // public static final String[] QUESTION_Types = new String[]{"Yes_No", "Number", "Text", "Date", "Long_Text", "Rating", "Picklist", "Multi_Select", "Photo"};

    public static String getCheckLists() {
       List<Checklist__c> checklists = getAllChecklists(); 
       return JSON.serialize(checklists);
    } 

    @RemoteAction
    global static List<Checklist_Response__c> pending_checklists() {
       List<Checklist_Response__c> checklists = [SELECT Id, Checklist__r.Name, Checklist__r.Description__c, 
                                                Checklist__r.Id FROM Checklist_Response__c WHERE Status__c=:'Pending'];
       return checklists;
    } 

    @RemoteAction
    global static List<Checklist_Response__c> completed_checklists() {
       List<Checklist_Response__c> checklists = [SELECT Id, Checklist__r.Name, Checklist__r.Description__c, 
                                                Checklist__r.Id FROM Checklist_Response__c WHERE Status__c=:'Complete'];
       return checklists;
    } 

    public static List<Checklist__c> getAllChecklists(){
        return [SELECT Id, Name, Description__c FROM Checklist__c];
    }

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

    public static Checklist_Response__c save_helper(String checkistResp, List<Checklist_Item_Response__c> responses) {
        List<Checklist_Response__c> res = [SELECT Id FROM Checklist_Response__c WHERE Id=:checkistResp];
        if (checkistResp != null && checkistResp != '' && res.size()>0) {
            List<Checklist_Item_Response__c> finalResponses = new List<Checklist_Item_Response__c>();
            for(Checklist_Item_Response__c resp : responses){
                if (resp.Answer__c != null){
                    resp.Answer__c = String.valueOf(resp.Answer__c);
                    finalResponses.add(resp);
                 }
            }
            upsert finalResponses;
            List<Checklist_Response__c> response = [SELECT Id, Status__c FROM Checklist_Response__c WHERE Id=:checkistResp];
            update response;
            return response[0];
        } else { // new response created
            Checklist_Response__c new_response = new Checklist_Response__c();
            new_response.Checklist__c = responses[0].Checklist_Item__r.Checklist__c;
            insert(new_response);
            List<Checklist_Item_Response__c> finalResponses = new List<Checklist_Item_Response__c>();
            for(Checklist_Item_Response__c resp : responses){
                resp.Checklist_Response__c = new_response.Id;
                if (resp.Answer__c != null){
                    resp.Answer__c = String.valueOf(resp.Answer__c);
                    finalResponses.add(resp);
                }
            }
            insert finalResponses;
            return new_response;
        }
    }

    @RemoteAction
    global static List<Checklist__c> save_responses(String checkistResp, List<Checklist_Item_Response__c> responses) {
        if (responses == null || responses.size() == 0)
            return new List<Checklist__c>();
        Checklist_Response__c r = save_helper(checkistResp, responses);
        r.Status__c = 'Pending';
        update r;
        return [SELECT Name, Description__c, Id FROM Checklist__c];
    }

    @RemoteAction
    global static List<Checklist__c> submit_responses(String checkistResp, List<Checklist_Item_Response__c> responses) {
        if (responses == null || responses.size() == 0)
            return new List<Checklist__c>();
        Checklist_Response__c r = save_helper(checkistResp, responses);
        r.Status__c = 'Complete';
        update r;
        return [SELECT Name, Description__c, Id FROM Checklist__c];
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
        if (to_return.size() == 0)
            return new List<Checklist_Item_Response__c>();
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
    global static Id photo_remotecall(Id id, Blob bitphoto) {
        if (id == null || bitphoto == null) {
            return null;
        }
        List<Attachment> a = [SELECT Id FROM Attachment WHERE ParentId=:id];
        if (a.size() != 0) {
            for (Integer i=0; i<a.size(); i++) {
                delete a[i];
            }
        }
        Attachment attach = new Attachment();
        attach.Body = bitphoto;
        attach.Name = 'Photo for response: ' + id;
        // attach.ContentType = ;
        attach.ParentID = id;
        insert attach;
        return attach.Id;
    }

}