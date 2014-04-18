/** A utility class that helps create Checklists and Checklist related items. */
global class ChecklistUtilities {

    /** Returns a new Checklist with the name LISTNAME, and the DESCRIPTION
      * while inserting it into the database. */
    global static Checklist__c createChecklist(String listName, String description) {
        Checklist__c checklist = new Checklist__c(Name = listName, Description__c = description);
        insert checklist;
        return checklist;
    }

    /** Removes a Checklist with the ID LISTID. */
    global static void removeChecklist(ID listID) {
        Checklist__c checklist = findChecklist(listID);
        delete checklist;
    }

    /** Queries the database for a Checklist with the ID LISTID. */
    global static Checklist__c findChecklist(ID listID) {
        Checklist__c[] checklist = [SELECT ID, Name, Description__c FROM Checklist__c WHERE ID=:listID];
        return checklist[0];
    }
}
