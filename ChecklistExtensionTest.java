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

    static Id itemMaker(ID d) {
        Checklist_Item__c item = new Checklist_Item__c();
        item.Checklist__c = d;
        insert item;
        return item.Id;
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
    
 
    
    static testmethod void testSaveResponse() {
        Id d = listMaker('Completed');
        Checklist_Response__c resp = [SELECT Id From Checklist_Response__c WHERE Checklist__c=:d];
        Id i = itemMaker(d);
        List<Checklist_Item_Response__c> items = ChecklistExtension.getAllChecklistItems(d);
        items[0].Answer__c = 'Yes';
        items[0].Checklist_Response__c = resp.Id;
        ChecklistExtension.saveResponses(resp.Id, items);
        items = ChecklistExtension.editChecklistItems(resp.Id);
        System.assertEquals('Yes', items[0].Answer__c);
        resp = [SELECT status__c From Checklist_Response__c WHERE Checklist__c=:d];
        System.assertEquals('Pending', resp.Status__c);
    }
    
    static testmethod void testSubmitResponse() {
        Id d = listMaker('Completed');
        Checklist_Response__c resp = [SELECT Id From Checklist_Response__c WHERE Checklist__c=:d];
        Id i = itemMaker(d);
        List<Checklist_Item_Response__c> items = ChecklistExtension.getAllChecklistItems(d);
        for (Integer j = 0; j < items.size(); j++) {
            items[j].Answer__c = 'No';
            items[j].Checklist_Response__c = resp.Id;
        }
        Id r = ChecklistExtension.submitResponses(d, items, 0, 1);
        items = ChecklistExtension.editChecklistItems(r);
        System.assertEquals('No', items[0].Answer__c);
        resp = [SELECT status__c, Location__latitude__s, Location__longitude__s From Checklist_Response__c WHERE Id=:r];
        System.assertEquals('Complete', resp.Status__c);
        System.assertEquals(0 , resp.Location__latitude__s);
        System.assertEquals(1, resp.Location__longitude__s);
    }
}