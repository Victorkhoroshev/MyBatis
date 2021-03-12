package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.*;
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
    private final Gson gson;
    private final IdeaService ideaService;

    public IdeaServiceTest() {
        MockitoAnnotations.initMocks(this);
        gson = new Gson();
        ideaService = new IdeaService(contextService, gson, sessionService);
    }

    @Test
    public void getIdeasTest_Success() {
        when(ideaService.getSessionService().isLogin(anyString()))
                .thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
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
        assertEquals(gson.toJson(new GetAllIdeasDtoResponse(sortedMap)),
                ideaService.getIdeas(gson.toJson(new GetAllIdeasDtoRequest(randomString()))));
    }

    @Test
    public void getIdeasTest_Logout() {
        when(ideaService.getSessionService().isLogin(anyString()))
                .thenReturn(gson.toJson(new IsLoginDtoResponse(false)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage())),
                ideaService.getIdeas(gson.toJson(new GetAllIdeasDtoRequest(randomString()))));
    }

    @Test
    public void getIdeasTest_Field_Not_Valid() {
        when(ideaService.getSessionService().isLogin(anyString()))
                .thenReturn(gson.toJson(new IsLoginDtoResponse(false)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.getIdeas(gson.toJson(new GetAllIdeasDtoRequest(null))));
    }

    @Test
    public void getIdeasTest_Json_Is_Null() {
        when(ideaService.getSessionService().isLogin(anyString()))
                .thenReturn(gson.toJson(new IsLoginDtoResponse(false)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.getIdeas(null));
    }

    @Test
    public void addIdeaTest_Success() throws ServerException {
        Voter voter = getNewVoter();
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString()))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals(ideaService.addIdea(gson.toJson(new AddIdeaDtoRequest("idea", randomString()))),
                gson.toJson(new AddIdeaDtoResponse(ideaService.getIdeas().get(0).getKey())));
    }

    @Test
    public void addIdeaTest_Election_Start() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())),
                ideaService.addIdea(gson.toJson(new AddIdeaDtoRequest("idea", randomString()))));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void addIdeaTest_Field_Not_Valid() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.addIdea(gson.toJson(new AddIdeaDtoRequest(null, randomString()))));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void addIdeaTest_Json_Is_Null() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.addIdea(null));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void setIdeaCommunityTest_Success() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea(randomString(), getNewVoter(), randomString());
        idea1.setCommunity(true);
        ideaService.getIdeas().add(idea1);
        ideaService.getIdeas().add(new Idea(randomString(), getNewVoter(), randomString()));
        Idea idea = new Idea(randomString(), voter, randomString());
        ideaService.getIdeas().add(idea);
        ideaService.setIdeaCommunity(voter.getLogin());
        assertTrue(gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest(idea.getKey()))), GetIdeaDtoResponse.class).getIdea().isCommunity());
    }

    @Test
    public void estimateTest_Success() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", getNewVoter(), "text1");
        Idea idea2 = new Idea("2", voter, "text2");
        Idea idea3 = new Idea("3", getNewVoter(), "text3");
        ideaService.getIdeas().add(idea1);
        ideaService.getIdeas().add(idea2);
        ideaService.getIdeas().add(idea3);
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString()))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals("", ideaService.estimate(
                gson.toJson(new EstimateIdeaDtoRequest("1", 1, randomString()))));
        assertEquals(3, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("1"))), GetIdeaDtoResponse.class).getIdea().getRating());
    }

    @Test
    public void estimateTest_Voter_Want_Estimate_Yourself_Idea() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", voter, "text1");
        ideaService.getIdeas().add(idea1);
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString()))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        ideaService.estimate(gson.toJson(new EstimateIdeaDtoRequest("1", 1, randomString())));
        assertEquals(5, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("1"))), GetIdeaDtoResponse.class).getIdea().getRating());
    }

    @Test
    public void estimateTest_Voter_Already_Estimate() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", getNewVoter(), "text1");
        idea1.getVotedVoters().put(voter.getLogin(), 5);
        ideaService.getIdeas().add(idea1);
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString()))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        ideaService.estimate(gson.toJson(new EstimateIdeaDtoRequest("1", 1, randomString())));
        assertEquals(5, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("1"))), GetIdeaDtoResponse.class).getIdea().getRating());
    }

    @Test
    public void estimateTest_Election_Start() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())),
                ideaService.estimate(gson.toJson(new EstimateIdeaDtoRequest("1", 1, randomString()))));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void estimateTest_Field_Not_Valid() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.estimate(gson.toJson(new EstimateIdeaDtoRequest(null, 1, randomString()))));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void estimateTest_Json_IS_Null() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.estimate(null));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void changeRatingTest_Success() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", getNewVoter(), "text1");
        Idea idea2 = new Idea("2", getNewVoter(), "text2");
        Idea idea3 = new Idea("3", getNewVoter(), "text3");
        idea1.getVotedVoters().put(voter.getLogin(), 1);
        idea1.setRating(3f);
        idea1.setSum(6);
        ideaService.getIdeas().add(idea3);
        ideaService.getIdeas().add(idea2);
        ideaService.getIdeas().add(idea1);
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString()))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals("", ideaService.changeRating(
                gson.toJson(new ChangeRatingDtoRequest("", "1", 5))));
        assertEquals(5, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("1"))), GetIdeaDtoResponse.class).getIdea().getRating());
    }

    @Test
    public void changeRatingTest_Want_Estimate_Yourself_Idea() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", voter, "text1");
        ideaService.getIdeas().add(idea1);
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString())).thenReturn(
                gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals("", ideaService.changeRating(
                gson.toJson(new ChangeRatingDtoRequest("", "1", 1))));
        assertEquals(5, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("1"))), GetIdeaDtoResponse.class).getIdea().getRating());
    }

    @Test
    public void changeRatingTest_Not_Estimate() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", getNewVoter(), "text1");
        ideaService.getIdeas().add(idea1);
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString()))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals("", ideaService.changeRating(
                gson.toJson(new ChangeRatingDtoRequest("", "1", 1))));
        assertEquals(5, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("1"))), GetIdeaDtoResponse.class).getIdea().getRating());
    }

    @Test
    public void changeRatingTest_Election_Start() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())),
                ideaService.changeRating(gson.toJson(new ChangeRatingDtoRequest("", "1", 1))));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void changeRatingTest_Field_Not_Valid() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.changeRating(gson.toJson(new ChangeRatingDtoRequest(null, "1", 1))));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void changeRatingTest_Json_Is_Null() throws ServerException {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.changeRating(null));
        verify(ideaService.getSessionService(), times(0)).getVoter(anyString());
    }

    @Test
    public void removeRatingTest_Success() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", getNewVoter(), "text1");
        Idea idea2 = new Idea("2", getNewVoter(), "text2");
        idea1.getVotedVoters().put(voter.getLogin(), 1);
        idea1.setRating(3f);
        idea1.setSum(6);
        ideaService.getIdeas().add(idea2);
        ideaService.getIdeas().add(idea1);
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString()))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals("", ideaService.removeRating(
                gson.toJson(new RemoveRatingDtoRequest("", "1"))));
        assertEquals(5, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("1"))), GetIdeaDtoResponse.class).getIdea().getRating());
    }

    @Test
    public void removeRatingTest_Want_Remove_Yourself_Rating() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", voter, "text1");
        idea1.getVotedVoters().put(getNewVoter().getLogin(), 1);
        idea1.setRating(3f);
        idea1.setSum(6);
        ideaService.getIdeas().add(idea1);
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString())).thenReturn(
                gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals("", ideaService.removeRating(
                gson.toJson(new RemoveRatingDtoRequest("", "1"))));
        assertEquals(3, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("1"))), GetIdeaDtoResponse.class).getIdea().getRating());
    }

    @Test
    public void removeRatingTest_Not_Estimate() throws ServerException {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", getNewVoter(), "text1");
        ideaService.getIdeas().add(idea1);
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoter(anyString())).thenReturn(
                gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals("", ideaService.removeRating(
                gson.toJson(new RemoveRatingDtoRequest("", "1"))));
        assertEquals(5, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("1"))), GetIdeaDtoResponse.class).getIdea().getRating());
    }

    @Test
    public void removeRatingTest_Election_Start() {
        when(ideaService.getContextService().isElectionStart()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())),
                ideaService.removeRating(gson.toJson(new RemoveRatingDtoRequest("", "1"))));
    }

    @Test
    public void removeRatingTest_Field_Not_Valid() {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.removeRating(gson.toJson(new RemoveRatingDtoRequest(null, "1"))));
    }

    @Test
    public void removeRatingTest_Json_Is_Null() {
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.removeRating(null));
    }

    @Test
    public void removeAllRatingTest_Success() {
        Voter voter = getNewVoter();
        Idea idea1 = new Idea("1", voter, "text1");
        Idea idea2 = new Idea("2", getNewVoter(), "text2");
        idea2.getVotedVoters().put(voter.getLogin(), 3);
        ideaService.getIdeas().add(idea1);
        ideaService.getIdeas().add(idea2);
        ideaService.removeAllRating(gson.toJson(new RemoveAllRatingDtoRequest(voter)));
        assertTrue(idea1.isCommunity());
        assertEquals(1, idea2.getVotedVoters().size());
    }

    @Test
    public void removeAllRatingTest_Not_Estimate() {
        Idea idea1 = new Idea("1", getNewVoter(), "text1");
        Idea idea2 = new Idea("2", getNewVoter(), "text2");
        ideaService.getIdeas().add(idea1);
        ideaService.getIdeas().add(idea2);
        ideaService.removeAllRating(gson.toJson(new RemoveAllRatingDtoRequest(getNewVoter())));
        assertEquals(2, ideaService.getIdeas().size());
    }

    @Test
    public void addAllIdeasTest_Success() throws ServerException {
        Voter voter = getNewVoter();
        when(ideaService.getContextService().isElectionStart()).thenReturn(false);
        when(ideaService.getSessionService().getVoterSession(anyString())).thenReturn(
                gson.toJson(new GetVoterSessionDtoResponse(new Session("1"))));
        when(ideaService.getSessionService().getVoter(anyString())).thenReturn(
                gson.toJson(new GetVoterDtoResponse(voter)));
        List<String> ideas = new ArrayList<>();
        ideas.add("text1");
        ideas.add("text2");
        ideas.add("text3");
        ideas.add("text4");
        ideaService.addAllIdeas(gson.toJson(new AddAllIdeasDtoRequest(voter, ideas)));
        assertEquals(4, ideaService.getIdeas().size());
    }

    @Test
    public void getIdeaTest_Success() throws ServerException {
        Idea idea1 = new Idea("1", getNewVoter(), "");
        Idea idea2 = new Idea("2", getNewVoter(), "");
        Idea idea3 = new Idea("3", getNewVoter(), "");
        Idea idea4 = new Idea("4", getNewVoter(), "");
        ideaService.getIdeas().add(idea1);
        ideaService.getIdeas().add(idea2);
        ideaService.getIdeas().add(idea3);
        ideaService.getIdeas().add(idea4);
        assertEquals(idea4, gson.fromJson(ideaService.getIdea(
                gson.toJson(new GetIdeaDtoRequest("4"))), GetIdeaDtoResponse.class).getIdea());
    }

    @Test
    public void getIdeaTest_Not_Found() {
        try {
            ideaService.getIdea(gson.toJson(new GetIdeaDtoRequest(randomString())));
        } catch (ServerException ex) {
            assertEquals(ExceptionErrorCode.IDEA_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Test
    public void getAllVotersIdeasTest_Success() {
        Voter voter = getNewVoter();
        Voter voter2 = getNewVoter();
        Idea idea1 = new Idea("1", voter, "1");
        Idea idea2 = new Idea("2", voter2, "1");
        Idea idea3 = new Idea("3", getNewVoter(), "1");
        Idea idea4 = new Idea("4", getNewVoter(), "1");
        idea4.setCommunity(true);
        ideaService.getIdeas().add(idea1);
        ideaService.getIdeas().add(idea2);
        ideaService.getIdeas().add(idea3);
        ideaService.getIdeas().add(idea4);
        List<String> logins = new ArrayList<>();
        logins.add(voter.getLogin());
        logins.add(voter2.getLogin());
        List<Idea> ideas = new ArrayList<>();
        ideas.add(idea1);
        ideas.add(idea2);
        when(ideaService.getSessionService().isLogin(anyString())).thenReturn(
                gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new GetAllVotersIdeasDtoResponse(ideas)),
                ideaService.getAllVotersIdeas(gson.toJson(new GetAllVotersIdeasDtoRequest(randomString(), logins))));
    }

    @Test
    public void getAllVotersIdeasTest_Contains_Community() {
        Idea idea1 = new Idea("1", getNewVoter(), "1");
        Idea idea2 = new Idea("2", getNewVoter(), "1");
        idea1.setCommunity(true);
        idea2.setCommunity(true);
        ideaService.getIdeas().add(idea1);
        ideaService.getIdeas().add(idea2);
        List<String> logins = new ArrayList<>();
        logins.add(null);
        List<Idea> ideas = new ArrayList<>();
        ideas.add(idea1);
        ideas.add(idea2);
        when(ideaService.getSessionService().isLogin(anyString()))
                .thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new GetAllVotersIdeasDtoResponse(ideas)),
                ideaService.getAllVotersIdeas(gson.toJson(new GetAllVotersIdeasDtoRequest(randomString(), logins))));
    }

    @Test
    public void getAllVotersIdeasTest_Logout() {
        when(ideaService.getSessionService().isLogin(anyString()))
                .thenReturn(gson.toJson(new IsLoginDtoResponse(false)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage())),
                ideaService.getAllVotersIdeas(gson.toJson(new GetAllVotersIdeasDtoRequest(randomString(), new ArrayList<>()))));
    }

    @Test
    public void getAllVotersIdeasTest_Field_Not_Valid() {
        when(ideaService.getSessionService().isLogin(anyString()))
                .thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.getAllVotersIdeas(
                        gson.toJson(new GetAllVotersIdeasDtoRequest(null, new ArrayList<>()))));
    }

    @Test
    public void getAllVotersIdeasTest_Json_Is_Null() {
        when(ideaService.getSessionService().isLogin(anyString())).thenReturn(
                gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                ideaService.getAllVotersIdeas(null));
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