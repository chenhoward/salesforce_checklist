/** The test for ChecklistExtension. */
@isTest
public class ChecklistExtensionTest{

    static Id listMaker(String s) {
        Checklist__c checklist = ChecklistUtilities.createChecklist('c', null);
        Checklist_Response__c response = new Checklist_Response__c();
        response.Checklist__c = checklist.Id;
        response.Status__c = s;
        response.Responder__c = UserInfo.getUserId();
        Checklist_Item__c item = new Checklist_Item__c();
        item.Checklist__c = checklist.Id;
        insert item;
        insert response;
        return checklist.Id;
    }

    static testmethod void testPending() {
        listMaker('Pending');
        System.assertEquals(ChecklistExtension.pendingChecklists().size(), 1);
    }

    static testmethod void testCompleted() {
        listMaker('Complete');
        System.assertEquals(ChecklistExtension.completedChecklists().size(), 1);
    }
    
    static testmethod void testGetChecklistItems() {
        Id d= listMaker('Compete');
        System.assertEquals(ChecklistExtension.getAllChecklistItems(d).size(), 1);
    }
    
    static testmethod void testEditChecklistItems() {
        Id d = listMaker('Test');
        System.assertEquals(ChecklistExtension.editChecklistItems(d).size(), 0);
    }
}