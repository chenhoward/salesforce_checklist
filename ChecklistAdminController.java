/** The controller for the Admin Page. */
global with sharing class ChecklistAdminController {

    public static String getCheckLists() {
       List<Checklist__c> checklists = getAllChecklists(); 
       return JSON.serialize(checklists);
    } 

    public static List<Checklist__c> getAllChecklists(){
        return [SELECT Name, Description__c, Id FROM Checklist__c];
    }

    /** Remote call used by admin pages to create a checklist. */
    @RemoteAction
    global static Checklist__c createChecklist(String listName, String description) {
        Checklist__c checklist = ChecklistUtilities.createChecklist(listName, description);
        return checklist;
    }

    /** Remote call that creates a checklist called LISTNAME with a DESCRIPTION with a list of Checklist ITEMS. */
    @RemoteAction
    global static Checklist__c createChecklistWithQuestions(String listName, String description, Checklist_Item__c[] items) {
        Checklist__c checklist = ChecklistUtilities.createChecklist(listName, description);
        ChecklistUtilities.addChecklistItems(checklist, items);
        return checklist;
    }
}