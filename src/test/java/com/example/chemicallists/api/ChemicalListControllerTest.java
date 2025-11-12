package com.example.chemicallists.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChemicalListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndFetchList() throws Exception {
        CreateChemicalListRequest request = new CreateChemicalListRequest("labchem", "Laboratory Chemicals", "Common lab chemicals");

        String response = mockMvc.perform(post("/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ChemicalListResponse created = objectMapper.readValue(response, ChemicalListResponse.class);
        assertThat(created.listId()).isPositive();
        assertThat(created.label()).isEqualTo("labchem");

        mockMvc.perform(get("/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].label").value("labchem"));

        mockMvc.perform(get("/lists/{id}", created.listId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laboratory Chemicals"));
    }

    @Test
    void updateListAndManageChemicals() throws Exception {
        CreateChemicalListRequest request = new CreateChemicalListRequest("indsolv", "Industrial Solvents", "Initial");
        String createResponse = mockMvc.perform(post("/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ChemicalListResponse created = objectMapper.readValue(createResponse, ChemicalListResponse.class);

        UpdateChemicalListRequest updateRequest = new UpdateChemicalListRequest("indsolv", "Industrial Solvents (Updated)", "Updated description");
        mockMvc.perform(patch("/lists/{id}", created.listId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description"));

        List<String> ciriIds = List.of("CIRI-0001", "CIRI-0002");
        mockMvc.perform(post("/lists/{id}/chemicals", created.listId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ciriIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.added.length()").value(2));

        mockMvc.perform(get("/lists/{id}/chemicals", created.listId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsInAnyOrder("CIRI-0001", "CIRI-0002")));

        mockMvc.perform(delete("/lists/{listId}/chemicals/{ciriId}", created.listId(), "CIRI-0001"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/lists/{id}", created.listId()))
                .andExpect(status().isNoContent());
    }
}
