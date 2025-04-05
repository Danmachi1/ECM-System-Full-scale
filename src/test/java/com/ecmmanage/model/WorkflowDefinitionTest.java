package com.ecmmanage.model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class WorkflowDefinitionTest {

    @Test
    void testCreateWorkflowDefinition() {
        List<String> steps = Arrays.<String>asList("Step 1", "Approval Step", "Final Step");
        List<String> approvalSteps = Arrays.<String>asList("Approval Step");
        List<String> automationSteps = Arrays.<String>asList("Automation Step 1");

        // Ensure correct constructor usage
        WorkflowDefinition workflowDefinition = new WorkflowDefinition("TestWorkflow", steps, approvalSteps, automationSteps);

        assertNotNull(workflowDefinition);
        assertEquals("TestWorkflow", workflowDefinition.getWorkflowName());
        assertEquals(3, workflowDefinition.getSteps().size());
        assertTrue(workflowDefinition.getSteps().contains("Approval Step"));
        assertEquals(1, workflowDefinition.getApprovalSteps().size());
        assertEquals(1, workflowDefinition.getAutomationSteps().size());
    }

    @Test
    void testModifyWorkflowSteps() {
        WorkflowDefinition workflowDefinition = new WorkflowDefinition();
        workflowDefinition.setWorkflowName("ModifiedWorkflow");
        workflowDefinition.setSteps(Arrays.<String>asList("New Step 1", "New Step 2"));
        workflowDefinition.setApprovalSteps(Arrays.<String>asList("New Approval Step"));
        workflowDefinition.setAutomationSteps(Arrays.<String>asList("New Automation Step"));

        assertEquals("ModifiedWorkflow", workflowDefinition.getWorkflowName());
        assertEquals(2, workflowDefinition.getSteps().size());
        assertEquals("New Step 1", workflowDefinition.getSteps().get(0));
        assertEquals(1, workflowDefinition.getApprovalSteps().size());
        assertEquals(1, workflowDefinition.getAutomationSteps().size());
    }
}
