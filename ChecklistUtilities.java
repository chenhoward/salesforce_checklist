/** A utility class that helps create Checklists and Checklist related items. */
global class ChecklistUtilities {

    /** Returns a new Checklist with the name LISTNAME and inserts it into the
     *  database. */
    global static Checklist__c createChecklist(String listName) {
        Checklist__c checklist = new Checklist__c(name = listName);
        insert checklist;
        return Checklist__c;
    }

    global static void removeChecklist(ID listID) {
        Checklist__c checklist = [SELECT Name, Id FROM Checklist__c WHERE Id := listID];
        remove checklist;
    }
}
