/** The test for ChecklistUtilities. */
@isTest
public class ChecklistUtilitiesTest {

    /** Tests the creation of a Checklist. */
    static testMethod void testCreateChecklist() {
    	Checklist__c test;
        test = ChecklistUtilities.createChecklist(null, null);
        test = ChecklistUtilities.findChecklist(test.ID);
        System.assertNotEquals(test, null);
        test = ChecklistUtilities.createChecklist('a', 'b');
        test = ChecklistUtilities.findChecklist(test.ID);
        System.assertNotEquals(test, null);
        System.assertEquals(test.Name, 'a');
        System.assertEquals(test.Description__c, 'b');
        ChecklistUtilities.removeChecklist(test.ID);
        test = ChecklistUtilities.findChecklist(test.ID);
        System.assertEquals(test, null);
    }

/** Tests the creation of a Checklist item. */
    static testMethod void testCreateChecklistItem() {
    	Checklist__c checklist= ChecklistUtilities.createChecklist('nom', 'desc');
    	Checklist_Item__c item1 = new Checklist_Item__c(Order__c = 0, Question__c = 'Age?',
    		Required__c = true);
    	Checklist_Item__c item2 = new Checklist_Item__c(Order__c = 0, Question__c = 'Size?',
    		Required__c = true);
        Checklist_Item__c[] itemList = new Checklist_Item__c[]{item1, item2};
    	ChecklistUtilities.addChecklistItems(checklist, itemList);
        Checklist_Item__c[] itemListCheck = ChecklistUtilities.findChecklistItems(checklist);
        System.assertEquals(itemListCheck.size(), 2);
    }
}