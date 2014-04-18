/** The test for ChecklistUtilities. */
private class ChecklistUtilities {

    /** Tests the creation of a checkList. */
    static testMethod void testCreateChecklistUtilities() {
    	Checklist__c test;
        test = ChecklistUtilities.createChecklist(null, null);
        test = ChecklistUtilities.findChecklist(test.ID);
        System.assertNotEquals(test, null);
        test = ChecklistUtilities.createChecklist("", "");
        test = ChecklistUtilities.findChecklist(test.ID);
        System.assertNotEquals(test, null);
        System.assertEquals(test.Name, "");
        System.assertEquals(test.Description__c, "");
        test = ChecklistUtilities.createChecklist("testName", "testDesc");
        test = ChecklistUtilities.findChecklist(test.ID);
        System.assertNotEquals(test, null);
        System.assertEquals(test.Name, "testName");
        System.assertEquals(test.Description__c, "testDesc");
        ChecklistUtilities.removeChecklist(test.ID);
        test = ChecklistUtilities.findChecklist(test.ID);
        System.assertEquals(test, null);
    }
}