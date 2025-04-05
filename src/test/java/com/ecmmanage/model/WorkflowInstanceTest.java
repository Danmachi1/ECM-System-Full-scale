package com.ecmmanage.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class WorkflowInstanceTest {

    private WorkflowInstance workflowInstance;

    @BeforeEach
    void setUp() {
        workflowInstance = new WorkflowInstance(
            "Test Workflow",
            Arrays.asList("Step 1", "Step 2", "Final Step"),
            Arrays.asList("Approval Step"),
            Arrays.asList("Automation Step 1")
        );
    }

    @Test
    void testWorkflowInstanceCreation() {
        assertNotNull(workflowInstance, "Workflow instance should not be null.");
        assertEquals("Test Workflow", workflowInstance.getWorkflowName(), "Workflow name should be 'Test Workflow'.");
        assertEquals("Step 1", workflowInstance.getCurrentStep(), "Initial step should be 'Step 1'.");
    }

    @Test
    void testMoveToNextStep() {
        // Move to Step 2
        boolean moved = workflowInstance.moveToNextStep();
        assertTrue(moved, "Should move to next step.");
        assertEquals("Step 2", workflowInstance.getCurrentStep(), "Current step should be 'Step 2'.");

        // Move to Final Step
        moved = workflowInstance.moveToNextStep();
        assertTrue(moved, "Should move to final step.");
        assertEquals("Final Step", workflowInstance.getCurrentStep(), "Current step should be 'Final Step'.");

        // Should not move beyond Final Step
        moved = workflowInstance.moveToNextStep();
        assertFalse(moved, "Should not be able to move past the final step.");
        assertEquals("Final Step", workflowInstance.getCurrentStep(), "Current step should remain 'Final Step'.");
    }

    @Test
    void testWorkflowCompletion() {
        workflowInstance.moveToNextStep();
        workflowInstance.moveToNextStep();
        boolean moved = workflowInstance.moveToNextStep(); // No more steps available

        assertFalse(moved, "Should not move past final step.");
        assertEquals("Final Step", workflowInstance.getCurrentStep(), "Current step should remain 'Final Step'.");
    }

    @Test
    void testSetCompletedAt() {
        LocalDateTime completionTime = LocalDateTime.now();
        workflowInstance.setCompletedAt(completionTime);

        assertNotNull(workflowInstance.getCompletedAt(), "CompletedAt timestamp should not be null.");
        assertEquals(completionTime, workflowInstance.getCompletedAt(), "Completion timestamp should match.");
    }

    @Test
    void testApprovalRequiredStepsHandling() {
        assertTrue(workflowInstance.getApprovalRequiredSteps().contains("Approval Step"),
            "Approval required steps should include 'Approval Step'.");
        assertFalse(workflowInstance.getApprovalRequiredSteps().isEmpty(),
            "Approval required steps list should not be empty.");
    }

    @Test
    void testAutomationStepsHandling() {
        assertTrue(workflowInstance.getAutomationSteps().contains("Automation Step 1"),
            "Automation steps should include 'Automation Step 1'.");
        assertFalse(workflowInstance.getAutomationSteps().isEmpty(),
            "Automation steps list should not be empty.");
    }
}
