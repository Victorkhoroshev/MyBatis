package net.thumbtack.school.elections.server.service;
import com.google.gson.Gson;
import net.thumbtack.school.elections.server.Server;
import net.thumbtack.school.elections.server.database.Database;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.model.Idea;
import net.thumbtack.school.elections.server.model.Voter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
public class CandidateServiceTest {
    @Mock
    private VoterService voterService;
    @Mock
    private ContextService contextService;
    @Mock
    private IdeaService ideaService;
    @Mock
    private SessionService sessionService;
    private final Gson gson = new Gson();
    private final CandidateService candidateService;

    public CandidateServiceTest() {
        MockitoAnnotations.initMocks(this);
        candidateService = new CandidateService(contextService, gson, sessionService, voterService, ideaService);
    }

    @Test
    public void addCandidateTest_Dao_Not_Contain_And_Voter_Not_Vote() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login1111111", "Pas&77123");
        Voter candidate = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login2222222", "Pas&77123");
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getVoterService().get(anyString())).thenReturn(gson.toJson(new GetVoterByLoginDtoResponse(candidate)));
        assertEquals("", candidateService.addCandidate(gson.toJson(new AddCandidateDtoRequest("1", randomString()))));
        assertTrue(candidateService.getDao().contains("login2222222"));
    }

    @Test
    public void addCandidateTest_Dao_Not_Contain_And_Voter_Vote() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login33333333", "Pas&77123");
        Voter candidate = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login44444444", "Pas&77123");
        voter.setHasOwnCandidate(true);
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getVoterService().get(anyString())).thenReturn(gson.toJson(new GetVoterByLoginDtoResponse(candidate)));
        assertEquals("", candidateService.addCandidate(gson.toJson(new AddCandidateDtoRequest("2", randomString()))));
        assertFalse(candidateService.getDao().contains("login44444444"));
    }

    @Test
    public void addCandidateTest_Dao_Contain_And_Voter_Vote() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login5555555", "Pas&77123");
        Voter candidate = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login6666666", "Pas&77123");
        voter.setHasOwnCandidate(true);
        candidateService.getDao().save(new Candidate(candidate));
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getVoterService().get(anyString())).thenReturn(gson.toJson(new GetVoterByLoginDtoResponse(candidate)));
        assertEquals("", candidateService.addCandidate(gson.toJson(new AddCandidateDtoRequest("3", randomString()))));
    }

    @Test
    public void addCandidateTest_Dao_Contain_And_Voter_Not_Vote() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login777777", "Pas&77123");
        Voter candidate = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, "login8888888", "Pas&77123");
        candidateService.getDao().save(new Candidate(candidate));
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getVoterService().get(anyString())).thenReturn(gson.toJson(new GetVoterByLoginDtoResponse(candidate)));
        assertEquals("", candidateService.addCandidate(gson.toJson(new AddCandidateDtoRequest("4", randomString()))));
    }

    @Test
    public void addCandidateTest_Election_Start() {
        when(candidateService.getContextService().isElectionStart()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())), candidateService.addCandidate(gson.toJson(new AddCandidateDtoRequest(randomString(), randomString()))));
    }

    @Test
    public void addCandidateTest_Field_Not_Valid() {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.addCandidate(gson.toJson(new AddCandidateDtoRequest(null , randomString()))));
    }

    @Test
    public void addCandidateTest_Json_Null() {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.addCandidate(null));
    }

    @Test
    public void confirmationCandidacyTest_Success() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        ConfirmationCandidacyDtoRequest request = new ConfirmationCandidacyDtoRequest("1", new ArrayList<>());
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getIdeaService().getAllVotersIdeas(anyString())).thenReturn(gson.toJson(new GetAllVotersIdeasDtoResponse(new ArrayList<>())));
        assertEquals("", candidateService.confirmationCandidacy(gson.toJson(request)));
    }

    @Test
    public void confirmationCandidacyTest_Has_Own_Candidate() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        voter.setHasOwnCandidate(true);
        ConfirmationCandidacyDtoRequest request = new ConfirmationCandidacyDtoRequest("1", new ArrayList<>());
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getIdeaService().getAllVotersIdeas(anyString())).thenReturn(gson.toJson(new GetAllVotersIdeasDtoResponse(new ArrayList<>())));
        assertEquals("", candidateService.confirmationCandidacy(gson.toJson(request)));
    }

    @Test
    public void confirmationCandidacyTest_Dao_Contain_Candidate() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        candidateService.getDao().save(new Candidate(voter));
        ConfirmationCandidacyDtoRequest request = new ConfirmationCandidacyDtoRequest("1", new ArrayList<>());
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getIdeaService().getAllVotersIdeas(anyString())).thenReturn(gson.toJson(new GetAllVotersIdeasDtoResponse(new ArrayList<>())));
        assertEquals("", candidateService.confirmationCandidacy(gson.toJson(request)));
    }

    @Test
    public void confirmationCandidacyTest_Election_Start() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(true);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        ConfirmationCandidacyDtoRequest request = new ConfirmationCandidacyDtoRequest("1", new ArrayList<>());
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getIdeaService().getAllVotersIdeas(anyString())).thenReturn(gson.toJson(new GetAllVotersIdeasDtoResponse(new ArrayList<>())));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())), candidateService.confirmationCandidacy(gson.toJson(request)));
    }

    @Test
    public void confirmationCandidacyTest_Field_Not_Valid() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        candidateService.getDao().save(new Candidate(voter));
        ConfirmationCandidacyDtoRequest request = new ConfirmationCandidacyDtoRequest(null , new ArrayList<>());
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getIdeaService().getAllVotersIdeas(anyString())).thenReturn(gson.toJson(new GetAllVotersIdeasDtoResponse(new ArrayList<>())));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.confirmationCandidacy(gson.toJson(request)));
    }

    @Test
    public void confirmationCandidacyTest_Request_Is_Null() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        candidateService.getDao().save(new Candidate(voter));
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getIdeaService().getAllVotersIdeas(anyString())).thenReturn(gson.toJson(new GetAllVotersIdeasDtoResponse(new ArrayList<>())));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.confirmationCandidacy(null));
    }

    @Test
    public void withdrawCandidacyTest_Success() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        candidateService.getDao().save(new Candidate(voter));
        assertEquals("", candidateService.withdrawCandidacy(gson.toJson(new WithdrawCandidacyDtoRequest(randomString()))));
    }

    @Test
    public void withdrawCandidacyTest_Dao_Not_Contain() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.CANDIDATE_NOT_FOUND.getMessage())), candidateService.withdrawCandidacy(gson.toJson(new WithdrawCandidacyDtoRequest(randomString()))));
    }

    @Test
    public void withdrawCandidacyTest_Election_Start() {
        when(candidateService.getContextService().isElectionStart()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_START.getMessage())), candidateService.withdrawCandidacy(gson.toJson(new WithdrawCandidacyDtoRequest(randomString()))));
    }

    @Test
    public void withdrawCandidacyTest_Field_Not_Valid() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        candidateService.getDao().save(new Candidate(voter));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.withdrawCandidacy(gson.toJson(new WithdrawCandidacyDtoRequest(null))));
    }

    @Test
    public void withdrawCandidacyTest_Json_Is_Null() throws ServerException {
        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        candidateService.getDao().save(new Candidate(voter));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.withdrawCandidacy(null));
    }

    @Test
    public void logoutTest_Success() {
        assertEquals(gson.toJson(new ErrorDtoResponse("Невозможно разлогиниться, для начала," +
                " снимите свою кандидатуру с выборов.")), candidateService.logout(gson.toJson(new LogoutDtoRequest(randomString()))));
    }

    @Test
    public void logoutTest_Field_Not_Valid() {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.logout(gson.toJson(new LogoutDtoRequest(null))));
    }

    @Test
    public void logoutTest_Json_Is_Null() {
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.logout(null));
    }

    @Test
    public void isCandidateTest_Success() throws ServerException {
        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
                1, 1, randomString(), "Pas&77123");
        when(candidateService.getSessionService().getVoter(anyString())).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(candidateService.getIdeaService().getAllVotersIdeas(anyString())).thenReturn(gson.toJson(new GetAllVotersIdeasDtoResponse(new ArrayList<>())));
        candidateService.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(randomString(), new ArrayList<>())));
        assertTrue(candidateService.isCandidate(gson.toJson(new IsCandidateDtoRequest(randomString()))));
    }

    @Test
    public void isCandidateTest_Voter_Logout() throws ServerException {
        when(candidateService.getSessionService().getVoter(anyString())).thenThrow(new ServerException(ExceptionErrorCode.LOGOUT));
        when(candidateService.getIdeaService().getAllVotersIdeas(anyString())).thenReturn(gson.toJson(new GetAllVotersIdeasDtoResponse(new ArrayList<>())));
        candidateService.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(randomString(), new ArrayList<>())));
        assertFalse(candidateService.isCandidate(gson.toJson(new IsCandidateDtoRequest(randomString()))));
    }

//    @Test
//    public void addIdea_Success() throws ServerException {
//        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
//        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
//                1, 1, randomString(), "Pas&77123");
//        when(candidateService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString())))).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
//        candidateService.getDao().save(new Candidate(voter));
//        when(candidateService.getIdeaService().addIdea(anyString())).thenReturn(gson.toJson(new AddIdeaDtoResponse("key")));
//        when(candidateService.getIdeaService().getIdea(gson.toJson(new GetIdeaDtoRequest(anyString())))).thenReturn(gson.toJson(new GetIdeaDtoResponse(new Idea("key", voter, "idea"))));
//        //when(candidateService.getIdeaService().getIdea(gson.toJson(new GetIdeaDtoRequest(candidateService.getIdeaService().addIdea(gson.toJson(new AddIdeaDtoRequest("idea", "token"))))))).thenReturn(gson.toJson(new GetIdeaDtoResponse(new Idea("key", voter, "idea"))));
//        assertEquals(gson.toJson(new AddIdeaDtoResponse("key")), candidateService.addIdea(gson.toJson(new AddIdeaDtoRequest("idea", "token"))));
//    }

//    @Test
//    public void removeIdeaTest_Success() throws ServerException {
//        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
//                1, 1, randomString(), "Pas&77123");
//        candidateService.getDao().save(new Candidate(voter));
//        when(candidateService.getContextService().isElectionStart()).thenReturn(false);
//        when(candidateService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString())))).thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
//        when(candidateService.getIdeaService().getIdea(gson.toJson(new GetIdeaDtoRequest(anyString())))).thenReturn(gson.toJson(new GetIdeaDtoResponse(new Idea("key", voter, "idea"))));
//        assertEquals("", candidateService.removeIdea(gson.toJson(new RemoveIdeaDtoRequest("token", "key"))));
//    }

    @Test
    public void getCandidateMapTest_Success() {
        when(candidateService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new GetCandidateMapDtoResponse(new HashMap<>())), candidateService.getCandidateMap(gson.toJson(new GetCandidateMapDtoRequest(randomString()))));
    }

    @Test
    public void getCandidateMapTest_Logout() {
        when(candidateService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(false)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.LOGOUT.getMessage())), candidateService.getCandidateMap(gson.toJson(new GetCandidateMapDtoRequest(randomString()))));
    }

    @Test
    public void getCandidateMapTest_Field_Not_Valid() {
        when(candidateService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.getCandidateMap(gson.toJson(new GetCandidateMapDtoRequest(null))));
    }

    @Test
    public void getCandidateMapTest_Json_Not_Valid() {
        when(candidateService.getSessionService().isLogin(anyString())).thenReturn(gson.toJson(new IsLoginDtoResponse(true)));
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())), candidateService.getCandidateMap(null));
    }

    @Test
    public void getCandidateSetTest() {
        assertEquals(new HashSet<>(), candidateService.getCandidateSet());
    }

//    @Test
//    public void getCandidateTest_Success() throws ServerException {
//        Voter voter = new Voter(randomString(), randomString(), null, randomString(),
//                1, 1, randomString(), "Pas&77123");
//        Candidate candidate = new Candidate(voter);
//        candidateService.getSessionService().loginVoter(gson.toJson(new LoginVoterDtoRequest(voter)));
//        String token = gson.fromJson(candidateService.getSessionService().getVoterSession(gson.toJson(new GetVoterSessionDtoRequest(voter))), GetVoterSessionDtoResponse.class).getToken();
//        candidateService.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(token, new ArrayList<>())));
//        assertEquals(gson.toJson(new GetCandidateDtoResponse(candidate)), candidateService.getCandidate(gson.toJson(new GetCandidateDtoRequest(voter.getLogin()))));
//    }


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