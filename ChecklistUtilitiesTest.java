/** The test for ChecklistUtilities. */
@isTest
public class ChecklistUtilitiesTest {

    /** Tests the creation of a checkList. */
    static testMethod void testCreateChecklistUtilities() {
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
}