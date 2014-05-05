/** The test for ChecklistExtension. */
@isTest
public class ChecklistExtensionTest {

    /** Tests the completion of a Checklist Response. */
    static testMethod void test_finish_checklist_items() {
        Checklist__c testC = new Checklist__c(Name='Test Checklist');
        Checklist_Response__c newResp = new Checklist_Response__c(Status='Pending', Checklist=testC);
        Integer numPending = [SELECT count() FROM Checklist_Response__c WHERE Status__c = 'Pending'];
        Integer numCompleted = [SELECT count() FROM Checklist_Response__c WHERE Status__c = 'Complete'];
        Checklist__c test = ChecklistExtension.finish_checklist_items(newResp);
        Integer numPendingAfter = [SELECT count() FROM Checklist_Response__c WHERE Status__c = 'Pending'];
        Integer numCompletedAfter = [SELECT count() FROM Checklist_Response__c WHERE Status__c = 'Complete'];
        System.assertEquals(numPending-1, numPendingAfter);
        System.assertEquals(numCompleted+1, numCompletedAfter);
    }

    /** Tests the submission of Checklist responses. */
    static testMethod void test_submit_responses() {
        Checklist__c testC = new Checklist__c(Name='Test Checklist');
        
    }

    /** Tests the saving of Checklist responses. */
    static testMethod void test_save_responses() {
        System.assertEquals(itemListCheck.get(0).Question__c, 'Age?');
    }
}