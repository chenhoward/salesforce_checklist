/** The test for ChecklistUtilities. */
@isTest
public class ChecklistAdminControllerTest {

    /** Tests the creation of a Checklist. */
    static testMethod void testCreateChecklist() {
        Checklist__c test = ChecklistAdminController.createChecklist('Nom', 'Description');
        test = ChecklistUtilities.findChecklist(test.ID);
        System.assertNotEquals(test, null);
        System.assertEquals(test.Name, 'Nom');
        System.assertEquals(test.Description__c, 'Description');
        test.Name = 'newName';
        ChecklistAdminController.updateChecklist(test);
        test = ChecklistAdminController.getChecklist(test.ID);
        System.assertNotEquals(test, null);
        System.assertEquals(test.Name, 'newName');
        System.assertEquals(test.Description__c, 'Description');
        System.assertNotEquals(ChecklistAdminController.getCheckLists(), null);
    }

/** Tests the creation of a Checklist item. */
    static testMethod void testCreateChecklistItem() {
        Checklist_Item__c item1 = new Checklist_Item__c(Order__c = 5, Question__c = 'Age?',
            Required__c = true);
        Checklist_Item__c item2 = new Checklist_Item__c(Order__c = 0, Question__c = 'Size?',
            Required__c = true);
        Checklist_Item__c[] itemList = new Checklist_Item__c[]{item1, item2};
        Checklist__c checklist= ChecklistAdminController.createChecklistWithQuestions('nom', 'desc', itemList);
        System.assertEquals(itemList.get(0).Question__c, 'Age?');
        Checklist_Item__c[] itemListCheck = ChecklistAdminController.getChecklistItems(checklist);
        System.assertEquals(itemListCheck.size(), 2);
        System.assertEquals(itemListCheck.get(0).Question__c, 'Size?');
        item1.Order__c = 1;
        item2.Order__c = 2;
        ChecklistAdminController.updateChecklistItems(itemList);
        itemListCheck = ChecklistUtilities.findChecklistItems(checklist);
        System.assertEquals(itemListCheck.size(), 2);
        System.assertEquals(itemListCheck.get(0).Question__c, 'Age?');
    }
}