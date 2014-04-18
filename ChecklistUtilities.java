/** A utility class that helps create Checklists and Checklist related items. */
global class ChecklistUtilities {

    /** Returns a new Checklist with the name LISTNAME, and the description DESC
      * while inserting it into the database. */
    global static Checklist__c createChecklist(String listName, String desc) {
        Checklist__c checklist = new Checklist__c(Name = listName, Description__c = desc);
        insert checklist;
        return checklist;
    }

    /** Removes a Checklist with the ID LISTID. */
    global static void removeChecklist(ID listID) {
        remove findChecklist(listID);
    }

    /** Queries the database for a Checklist with the ID LISTID. */
    global static Checklist__c findChecklist(ID listID) {
        return [SELECT Name, Id, Description__c FROM Checklist__c WHERE Id := listID];
    }
}
