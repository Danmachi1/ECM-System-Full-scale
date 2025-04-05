package com.ecmmanage.ejb;

import com.ecmmanage.model.WorkflowInstance;
import com.ecmmanage.repository.WorkflowInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkflowProcessorBeanTest {

    @Mock
    private WorkflowInstanceRepository workflowInstanceRepository;

    @InjectMocks
    private WorkflowProcessorBean workflowProcessorBean;

    private WorkflowInstance approvalInstance;

    @BeforeEach
    void setUp() {
        approvalInstance = new WorkflowInstance(
            "Test Workflow",
            Arrays.asList("Step 1", "Approval Step", "Final Step"),
            Arrays.asList("Approval Step"),
            Arrays.asList("Auto Process Step")
        );
        approvalInstance.setCurrentStep("Approval Step"); // ✅ Set to approval step directly for testing
    }

    @Test
    void testStartWorkflow() {
        List<String> steps = Arrays.asList("Step 1", "Approval Step", "Final Step");
        List<String> approvals = Arrays.asList("Approval Step");
        List<String> automation = Arrays.asList("Auto Step");

        when(workflowInstanceRepository.save(any(WorkflowInstance.class)))
            .thenAnswer(i -> i.getArguments()[0]);

        WorkflowInstance created = workflowProcessorBean.startWorkflow("Test Workflow", steps, approvals, automation);

        assertNotNull(created);
        assertEquals("Step 1", created.getCurrentStep());
    }

    @Test
    void testApproveWorkflowStep() {
        when(workflowInstanceRepository.findById(anyLong())).thenReturn(Optional.of(approvalInstance));
        when(workflowInstanceRepository.save(any(WorkflowInstance.class))).thenAnswer(i -> i.getArguments()[0]);

        WorkflowInstance result = workflowProcessorBean.approveWorkflowStep(1L);

        assertNotNull(result);
        assertEquals("Final Step", result.getCurrentStep()); // ✅ Moves to next step
    }

    @Test
    void testAutoProcessWorkflow() {
        approvalInstance.setCurrentStep("Step 1"); // ✅ Not approval step

        when(workflowInstanceRepository.findById(anyLong())).thenReturn(Optional.of(approvalInstance));
        when(workflowInstanceRepository.save(any(WorkflowInstance.class))).thenAnswer(i -> i.getArguments()[0]);

        WorkflowInstance result = workflowProcessorBean.autoProcessWorkflow(1L);

        assertNotNull(result);
        assertNotEquals("Step 1", result.getCurrentStep()); // ✅ Should advance
    }
}
