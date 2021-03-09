package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import net.bytebuddy.jar.asm.TypeReference;
import net.thumbtack.school.elections.server.Server;
import net.thumbtack.school.elections.server.dto.request.AddIdeaDtoRequest;
import net.thumbtack.school.elections.server.dto.request.GetAllIdeasDtoRequest;
import net.thumbtack.school.elections.server.dto.request.GetAllVotersIdeasDtoRequest;
import net.thumbtack.school.elections.server.dto.request.GetIdeaDtoRequest;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.model.Idea;
import net.thumbtack.school.elections.server.model.Voter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class IdeaServiceTest {
    @Mock
    private ContextService contextService;
    @Mock
    private SessionService sessionService;
    private Gson gson;
    private IdeaService ideaService;

    public IdeaServiceTest() {
        MockitoAnnotations.initMocks(this);
        gson = new Gson();
        ideaService = new IdeaService(contextService, gson, sessionService);
    }

    @Test
    public void getIdeasTest_Success() {
        when(ideaService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
        Map<Idea, Float> sortedMap = new TreeMap<>();
        List<Idea> ideas = new ArrayList<>();
        Idea idea1 = new Idea(randomString(), getNewVoter(), randomString());
        Idea idea2 = new Idea(randomString(), getNewVoter(), randomString());
        Idea idea3 = new Idea(randomString(), getNewVoter(), randomString());
        sortedMap.put(idea1, 5f);
        sortedMap.put(idea2, 5f);
        sortedMap.put(idea3, 5f);
        ideas.add(idea1);
        ideas.add(idea2);
        ideas.add(idea3);
        ideaService.getIdeas().addAll(ideas);
        assertEquals(gson.toJson(new GetAllIdeasDtoResponse(sortedMap)), ideaService.getIdeas(gson.toJson(new GetAllIdeasDtoRequest(randomString()))));
    }

    @Test
    public void getIdeasTest_Logout() {
        when(ideaService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(false)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage())), ideaService.getIdeas(gson.toJson(new GetAllIdeasDtoRequest(randomString()))));
    }

    @Test
    public void getIdeasTest_Field_Not_Valid() {
        when(ideaService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(false)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), ideaService.getIdeas(gson.toJson(new GetAllIdeasDtoRequest(null))));
    }

    @Test
    public void getIdeasTest_Json_Is_Null() {
        when(ideaService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(false)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), ideaService.getIdeas(null));
    }

    @Test
    public void addIdeaTest_Success() throws ServerException {
        Voter voter = getNewVoter();
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals(ideaService.addIdea(gson.toJson(new AddIdeaDtoRequest("idea", randomString()))), gson.toJson(new AddIdeaDtoResponse(ideaService.getIdeas().get(0).getKey())));
    }

    @Test
    public void addIdeaTest_Election_Start() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())),ideaService.addIdea(gson.toJson(new AddIdeaDtoRequest("idea", randomString()))));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void addIdeaTest_Field_Not_Valid() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),ideaService.addIdea(gson.toJson(new AddIdeaDtoRequest(null, randomString()))));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void addIdeaTest_Json_Is_Null() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),ideaService.addIdea(null));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void setIdeaCommunityTest_Success() throws ServerException {
        Voter voter = getNewVoter();
        ideaService.getIdeas().add(new Idea(randomString(), getNewVoter(), randomString()));
        ideaService.getIdeas().add(new Idea(randomString(), getNewVoter(), randomString()));
        Idea idea = new Idea(randomString(), voter, randomString());
        ideaService.getIdeas().add(idea);
        ideaService.setIdeaCommunity(voter.getLogin());
        idea.setCommunity(true);
//        String s = ideaService.getIdea(gson.toJson(new GetIdeaDtoRequest(idea.getKey())));
//        Idea idea1 = gson.fromJson(ideaService.getIdea(gson.toJson(new GetIdeaDtoRequest(idea.getKey()))), GetIdeaDtoResponse.class).getIdea();
//        assertEquals(gson.toJson(new GetIdeaDtoResponse(idea)), ideaService.getIdea(gson.toJson(new GetIdeaDtoRequest(idea.getKey()))));
//   //     assertNull(ideaService.getIdea(gson.toJson(new GetIdeaDtoRequest(idea.getKey()))));
        Voter voter1 = getNewVoter();
        Voter voter2 = getNewVoter();
        Voter voter3 = getNewVoter();
        Voter voter4 = getNewVoter();
        Voter voter5 = getNewVoter();
        Map<String, String> voterVoterMap = new HashMap<>();
        voterVoterMap.put(voter1.getLastName(), voter1.getLastName());
        voterVoterMap.put(voter2.getLastName(), voter2.getLastName());
        voterVoterMap.put(voter3.getLastName(), voter3.getLastName());
        voterVoterMap.put(voter4.getLastName(), voter4.getLastName());
        voterVoterMap.put(voter5.getLastName(), voter5.getLastName());
        String s1 = gson.toJson(voterVoterMap);
        Map<Voter, Voter> voterVoterMap1 = gson.fromJson(s1, voterVoterMap.getClass());

    }



    private Voter getNewVoter() {
        Random random = new Random();
        return new Voter(randomString(), randomString(),
                randomString(), random.nextInt(100), random.nextInt(100), randomString(),randomString());
    }

    private String randomString() {
        Random random = new Random();
        char[] sAlphabet = "АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзиклмнопрстуфхцчшщъыьэюя".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            stringBuilder.append(sAlphabet[random.nextInt(sAlphabet.length)]);
        }
        return stringBuilder.toString();
    }
}