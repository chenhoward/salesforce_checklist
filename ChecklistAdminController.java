/** The controller for the Admin Page. */
global with sharing class ChecklistAdminController {

    public ChecklistAdminController(ApexPages.StandardController controller) {
    }

    @RemoteAction
    global static Checklist__c getChecklist(ID checklistId) {
        return ChecklistUtilities.findChecklist(checklistID);
    }

    @RemoteAction
    global static Checklist_Item__c[] getChecklistItems(Checklist__c checklist) {
        return ChecklistUtilities.findChecklistItems(checklist);
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

    /** Updates the given CHECKLIST. */
    @RemoteAction
    global static Checklist__c updateChecklist(Checklist__c checklist) {
        return ChecklistUtilities.updateChecklist(checklist);
    }

    /** Updates the give Checklist ITEMS. */
    @RemoteAction
    global static Checklist_Item__c[] updateChecklistItems(Checklist_Item__c[] items) {
        upsert items;
        return items;
    }

}