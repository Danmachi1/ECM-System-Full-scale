package com.ecmmanage.ejb;

import com.ecmmanage.model.WorkflowInstance;
import com.ecmmanage.repository.WorkflowInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IBMWorkflowAdapterTest {

    @Mock
    private WorkflowProcessorBean workflowProcessorBean;

    @Mock
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private IBMWorkflowAdapter ibmWorkflowAdapter;

    private String sampleIBMWorkflowContent;

    @BeforeEach
    void setUp() {
        sampleIBMWorkflowContent = "<Step>Step 1</Step>\n<Step>Approval Step</Step>\n<Step>Final Step</Step>";
    }

    @Test
    void testUploadIBMWorkflowFile() throws IOException {
        // Defining required lists for workflowInstance
        String workflowId = "workflow";
        List<String> approvalSteps = Arrays.asList("Step 1", "Approval Step", "Final Step");
        List<String> approvers = new ArrayList<>();
        List<String> metadata = new ArrayList<>();

        when(multipartFile.getOriginalFilename()).thenReturn("workflow.xpd");
        when(multipartFile.getBytes()).thenReturn(sampleIBMWorkflowContent.getBytes());

        when(workflowProcessorBean.startWorkflow(anyString(), anyList(), anyList(), anyList()))
                .thenReturn(new WorkflowInstance(workflowId, approvalSteps, approvers, metadata));

        WorkflowInstance instance = ibmWorkflowAdapter.uploadIBMWorkflowFile(multipartFile);

        assertNotNull(instance, "Workflow instance should not be null");
        assertEquals("workflow", instance.getWorkflowName(), "Workflow name should be 'workflow'");
        verify(workflowProcessorBean, times(1)).startWorkflow(anyString(), anyList(), anyList(), anyList());
    }
}
