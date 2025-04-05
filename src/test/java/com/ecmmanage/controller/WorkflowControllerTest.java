package com.ecmmanage.controller;

import com.ecmmanage.ejb.WorkflowProcessorBean;
import com.ecmmanage.model.WorkflowInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkflowControllerTest {

    @Mock
    private WorkflowProcessorBean workflowProcessorBean;

    @InjectMocks
    private WorkflowController workflowController;

    private WorkflowInstance workflowInstance;

    @BeforeEach
    void setUp() {
        workflowInstance = new WorkflowInstance("Test Workflow",
                Arrays.asList("Step 1", "Approval Step", "Final Step"),
                Arrays.asList("Approval Step"),
                null);
    }

    @Test
    void testStartWorkflow() {
        // Correctly mock the method with four parameters
        when(workflowProcessorBean.startWorkflow(anyString(), anyList(), anyList(), any()))
                .thenReturn(workflowInstance);

        ResponseEntity<WorkflowInstance> response = workflowController.startWorkflow("Test Workflow");

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("Step 1", response.getBody().getCurrentStep());

        verify(workflowProcessorBean, times(1)).startWorkflow(anyString(), anyList(), anyList(), any());
    }
}
