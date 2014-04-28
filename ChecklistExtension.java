global with sharing class ChecklistExtension {

    public ChecklistExtension(ApexPages.StandardController stdController) {}

    public static String getCheckLists() {
       List<Checklist__c> checklists = getAllChecklists(); 
       return JSON.serialize(checklists);
    } 

    public static List<Checklist__c> getAllChecklists(){
        return [SELECT Name, Description__c, Id FROM Checklist__c];
    }

    public static List<Checklist_Item_Response__c> getAllChecklistItems(Id checklist){
        List<Checklist_Item__c> to_return = [SELECT Order__c, Question__c, Required__c, Type__c, Checklist__c 
                FROM Checklist_Item__c WHERE Checklist__c=:checklist AND isActive__c = True order by Order__c];

        List<Checklist_Item_Response__c> responses = new List<Checklist_Item_Response__c>();
        
        for (Checklist_Item__c item : to_return){
            Checklist_Item_Response__c resp = new Checklist_Item_Response__c(Checklist_Item__c = item.Id, 
                                                                             Checklist_Item__r = item);
            responses.add(resp);            
        }   
        return responses;
    }

    // Creates Checklist Response Item Objects
    @RemoteAction
    global static void save_responses(Id checklist, Id checkistResp, List<Checklist_Item_Response__c> responses) {
        List<Checklist_Response__c> response = [SELECT Checklist__c, Checklist__r.Id From Checklist_Response__c WHERE Id =: checklist];
        if (response.size() == 1) {
            List<Checklist_Item_Response__c> finalResponses = new List<Checklist_Item_Response__c>();
            for(Checklist_Item_Response__c resp : responses){
                if (resp.Checklist_Item__r.Type__c == 'Yes/No') {
                    resp.Answer__c = String.valueOf(resp.Answer__c);
                }
                System.debug(resp);
                finalResponses.add(resp);
            }
            update finalResponses;
        } else {
            Checklist_Response__c new_response = new Checklist_Response__c();
            new_response.Checklist__c = checklist;
            insert(new_response);
            List<Checklist_Item_Response__c> finalResponses = new List<Checklist_Item_Response__c>();
            for(Checklist_Item_Response__c resp : responses){
                resp.Checklist_Response__c = new_response.Id;
                if (resp.Answer__c != null){
                    if (resp.Checklist_Item__r.Type__c == 'Yes/No') {
                        resp.Answer__c = String.valueOf(resp.Answer__c);
                    }
                    finalResponses.add(resp);
                }
            }
            upsert finalResponses;
        }
        

        // insert(new_response);
        // List<Checklist_Item_Response__c> finalResponses = new List<Checklist_Item_Response__c>();
        // for(Checklist_Item_Response__c resp : responses){
        //     resp.Checklist_Response__c = new_response.Id;
        //     if (resp.Answer__c != null){
        //         if (resp.Checklist_Item__r.Type__c == 'Yes/No') {
        //             resp.Answer__c = String.valueOf(resp.Answer__c);
        //         }
        //         finalResponses.add(resp);
        //     }
        // }
        // insert finalResponses;
    }

    // Creates Checklist Response Item Objects
    @RemoteAction
    global static List<Checklist_Item_Response__c> checklist_items(Id checklist) {
        return getAllChecklistItems(checklist);
    }

    @RemoteAction
    global static List<Checklist_Item_Response__c> edit_checklist_items(Id checklist_response) {
        List<Checklist_Item_Response__c> to_return = [SELECT Answer__c, Checklist_Item__c, Checklist_Item__r.Order__c, Checklist_Item__r.Question__c, Checklist_Item__r.Checklist__c,
                                                      Checklist_Item__r.Required__c, Checklist_Item__r.Type__c
                                                      FROM Checklist_Item_Response__c WHERE Checklist_Response__c=:checklist_response 
                                                      order by Checklist_Item__r.Order__c];

       Map<Id, Checklist_Item_Response__c> checklistItemId2Resp = new Map<Id, Checklist_Item_Response__c>();


       if (to_return.size() == 0)
            return;
        // crosscheck with other questions to return a list of all questions
        List<Checklist_Item__c> to_return = [SELECT Order__c, Question__c, Required__c, Type__c, Checklist__c 
                FROM Checklist_Item__c WHERE Checklist__c=:to_return[0].Checklist_Item__r.Checklist__c AND isActive__c = True order by Order__c];
        return to_return;
    }

    @RemoteAction
    global static String lat_long(Id checklist_response) {
        Checklist_Response__c response = [SELECT Location__c FROM Checklist_Response__c WHERE Id=: checklist_response];
        return JSON.serialize(response);
    }

}