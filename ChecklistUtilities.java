/** A utility class that helps create Checklists and Checklist related items. */
global class ChecklistUtilities {

    /** Returns a new Checklist with the name LISTNAME, and the DESCRIPTION
      * while inserting it into the database. */
    global static Checklist__c createChecklist(String listName, String description) {
        if (listName == null) {
            listName = '';
        }
        if (description == null) {
            description = '';
        }
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
        if (checklist.size() == 0) {
            return null;
        }
        return checklist[0];
    }

    /** Inserts CHECKLISTITEMS into CHECKLIST and returns the list of inserted items. */
    global static Checklist_Item__c[] addChecklistItems(Checklist__c checklist, Checklist_Item__c[] checklistItems) {
         for(Checklist_Item__c item: checklistItems) {
             item.Checklist__c = checklist.ID;
         }
         insert checklistItems;
         return checklistItems;
    }

    /** Queries the database for a list of Checklist items. */
    global static Checklist_Item__c[] findChecklistItems(Checklist__c checklist) {
        Checklist_Item__c[] checklistItems= [SELECT Checklist__c, Order__c, Question__c, Required__C, Type__c, Values__c, isActive__c
         FROM Checklist_Item__c WHERE Checklist__c=:checklist.ID ORDER BY Order__c ASC];
        return checklistItems;
    }

    global static Checklist__c updateChecklist(Checklist__c checklist) {
        update checklist;
        return checklist;
    }

    global static Checklist_Item__c[] updateChecklistItems(Checklist_Item__c[] checklistItems) {
        update checklistItems;
        return checklistItems;
    }
}
