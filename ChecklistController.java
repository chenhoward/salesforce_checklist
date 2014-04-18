global with sharing class ChecklistController {

	public static String getCheckListItems() {
	   List<Checklist_Item__c> checklists = getAllChecklists(); // pass in ID
	   return JSON.serialize(checklists);
	} 

	private static List<Checklist_Item__c> getAllChecklists(){
        return [SELECT Order__c, Question__c, Required__c, Type__c, Checklist__c 
        		FROM Checklist_Item__c WHERE Checklist__c=:ApexPages.currentPage().getParameters().get('checklist_id')];
    }

    // Creates Checklist Response Item Objects
    @RemoteAction
    global static String[] save_responses(Id checklist, Id[] checklist_items, String[] answers) {
        Checklist_Reponse__c new_response = new Checklist_Reponse__c();
        new_response.Checklist__c = checklist;
        insert(new_response);

        for (Integer i=0; i<checklist_items.size(); i++) {
        	Checklist_Item_Response__c new_item_response = new Checklist_Item_Response__c();
	        new_item_response.Checklist_Item__c = checklist_items[i];
	        new_item_response.Checklist_Reponse__c = new_response.Id;
	        new_item_response.Answer__c = answers[i];
	        insert new_item_response;
        }
        return new String[]{}; // can include succes message?
    }
}