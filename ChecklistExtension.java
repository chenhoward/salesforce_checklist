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
                FROM Checklist_Item__c WHERE Checklist__c=:checklist order by Order__c];

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
    global static void save_responses(Id checklist, List<Checklist_Item_Response__c> responses) {
        Checklist_Response__c new_response = new Checklist_Response__c();
        new_response.Checklist__c = checklist;
        insert(new_response);

        List<Checklist_Item_Response__c> finalResponses = new List<Checklist_Item_Response__c>();
        for(Checklist_Item_Response__c resp : responses){
            resp.Checklist_Response__c = new_response.Id;
            System.debug(resp.Answer__c);
            if (resp.Answer__c != null && resp.Answer__c.trim() != ''){
                finalResponses.add(resp);
            }
        }
        insert finalResponses;
    }

    // Creates Checklist Response Item Objects
    @RemoteAction
    global static List<Checklist_Item_Response__c> checklist_items(Id checklist) {
        return getAllChecklistItems(checklist);
    }

    @RemoteAction
    global static String lat_long(Id checklist_response) {
        Checklist_Response__c response = [SELECT Location__c FROM Checklist_Response__c WHERE Id=: checklist_response];
        return JSON.serialize(response);
    }

}