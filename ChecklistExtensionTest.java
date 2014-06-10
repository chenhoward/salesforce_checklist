/** The test for ChecklistExtension. */
@isTest
public class ChecklistExtensionTest{

    static void listMaker(String s) {
        Checklist__c checklist = ChecklistUtilities.createChecklist('c', null);
        Checklist_Response__c response = new Checklist_Response__c();
        response.Checklist__c = checklist.Id;
        response.Status__c = s;
        response.Responder__c = UserInfo.getUserId();
        insert response;
    }
    static testmethod void testPending() {
	    listMaker('Pending');
        System.assertEquals(ChecklistExtension.pendingChecklists().size(), 1);
    }

    static testmethod void testCompleted() {
        listMaker('Complete');
        System.assertEquals(ChecklistExtension.completedChecklists().size(), 1);
    }
}