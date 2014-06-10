/** The test for ChecklistExtension. */
@isTest
public class ChecklistExtensionTest{

    static testmethod void testPending() {
	    Checklist__c checklist = ChecklistUtilities.createChecklist('c', null);
        Checklist_Response__c response = new Checklist_Response__c();
        response.Checklist__c = checklist.Id;
        response.Status__c = 'Pending';
        response.Responder__c = UserInfo.getUserId();
        insert response;
        System.assertEquals(ChecklistExtension.pendingChecklists().size(), 1);

    }

}