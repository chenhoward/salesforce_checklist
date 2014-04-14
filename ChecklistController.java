global with sharing class ChecklistController {

	public static String getCheckListItems() {
	   List<Checklist_Item__c> checklists = getAllChecklists(); // pass in ID
	   return JSON.serialize(checklists);
	} 

	private static List<Checklist_Item__c> getAllChecklists(){
        return [SELECT Order__c, Question__c, Required__c, Type__c, Checklist__c 
        		FROM Checklist_Item__c WHERE Checklist__c=:ApexPages.currentPage().getParameters().get('checklist_id')];
    }

}