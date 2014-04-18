global with sharing class ChecklistAdminController {


    /** Remote call used by admin pages to create a checklist. */
    @RemoteAction
    global static Checklist__c createChecklist(String listName, String description) {
        Checklist__c checklist = ChecklistUtilities.createChecklist(listName, description);
        return checklist;
    }
}