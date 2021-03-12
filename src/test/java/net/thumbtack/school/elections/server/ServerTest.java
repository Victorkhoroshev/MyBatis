package net.thumbtack.school.elections.server;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ServerTest {
    private Gson gson;
    @Mock
    private ContextService contextService;
    @Mock
    private SessionService sessionService;
    @Mock
    private IdeaService ideaService;
    @Mock
    private CandidateService candidateService;
    @Mock
    private VoterService voterService;
    @Mock
    private CommissionerService commissionerService;
    @Mock
    private ElectionService electionService;
    @InjectMocks
    private final Server server;


    public ServerTest() {
        gson = new Gson();
        server = new MyServer();
    }

    @Test
    public void startServerTest_Without_SaveDataFile() throws IOException, ClassNotFoundException {
        Server server1 = new Server();
        server1.startServer(null);
        assertAll(
                () -> assertNotNull(server1.getGson()),
                () -> assertNotNull(server1.getContextService()),
                () -> assertNotNull(server1.getSessionService()),
                () -> assertNotNull(server1.getIdeaService()),
                () -> assertNotNull(server1.getCandidateService()),
                () -> assertNotNull(server1.getVoterService()),
                () -> assertNotNull(server1.getElectionService()),
                () -> assertNotNull(server1.getCommissionerService())

        );
    }

    @Test
    public void stopServerTest_Without_SaveDataFile() throws IOException, ClassNotFoundException {
        Server server1 = new Server();
        server1.startServer(null);
        server1.stopServer(null);
        assertAll(
                () -> assertNull(server1.getGson()),
                () -> assertNull(server1.getContextService()),
                () -> assertNull(server1.getSessionService()),
                () -> assertNull(server1.getIdeaService()),
                () -> assertNull(server1.getCandidateService()),
                () -> assertNull(server1.getVoterService()),
                () -> assertNull(server1.getElectionService()),
                () -> assertNull(server1.getCommissionerService())

        );
    }

    @Test
    public void startStopServer_With_SaveDataFile() throws IOException, ClassNotFoundException {
        Server server1 = new Server();
        server1.startServer(null);
        server1.getContextService().getContext().setElectionStart(true);
        server1.stopServer(
                "C:\\Thumbtack\\thumbtack_online_school_2020_2__viktor_khoroshev\\saveDataFile");
        Server server2 = new Server();
        server2.startServer(
                "C:\\Thumbtack\\thumbtack_online_school_2020_2__viktor_khoroshev\\saveDataFile");
        assertTrue(server2.getContextService().isElectionStart());
        server2.stopServer(null);
    }

    @Test
    public void registerTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.register(gson.toJson(new RegisterDtoRequest(randomString(), randomString(), randomString(),
                randomString(), 1, 1, "Login11111", "Pass@#12314")));
        verify(server.getVoterService(), times(1)).register(anyString());
        server.stopServer(null);
    }

    @Test
    public void loginTest_Commissioner() throws IOException, ClassNotFoundException {
        server.startServer(null);
        when(server.getCommissionerService().isCommissioner(anyString())).thenReturn(true);
        server.login(gson.toJson(new LogoutDtoRequest(randomString())));
        verify(server.getCommissionerService(), times(1)).login(anyString());
        verify(server.getVoterService(), times(0)).login(anyString());
        server.stopServer(null);
    }

    @Test
    public void loginTest_Voter() throws IOException, ClassNotFoundException {
        server.startServer(null);
        when(server.getCommissionerService().isCommissioner(anyString())).thenReturn(false);
        server.login(gson.toJson(new LogoutDtoRequest(randomString())));
        verify(server.getCommissionerService(), times(0)).login(anyString());
        verify(server.getVoterService(), times(1)).login(anyString());
        server.stopServer(null);
    }

    @Test
    public void logoutTest_Commissioner() throws IOException, ClassNotFoundException {
        server.startServer(null);
        when(server.getCommissionerService().isCommissioner(anyString())).thenReturn(true);
        when(server.getCandidateService().isCandidate(anyString())).thenReturn(false);
        server.logout(randomString());
        verify(server.getCommissionerService(), times(1)).logout(anyString());
        verify(server.getVoterService(), times(0)).logout(anyString());
        verify(server.getCandidateService(), times(0)).logout(anyString());
        server.stopServer(null);
    }

    @Test
    public void logoutTest_Candidate() throws IOException, ClassNotFoundException {
        server.startServer(null);
        when(server.getCommissionerService().isCommissioner(anyString())).thenReturn(false);
        when(server.getCandidateService().isCandidate(anyString())).thenReturn(true);
        server.logout(randomString());
        verify(server.getCommissionerService(), times(0)).logout(anyString());
        verify(server.getVoterService(), times(0)).logout(anyString());
        verify(server.getCandidateService(), times(1)).logout(anyString());
        server.stopServer(null);
    }

    @Test
    public void logoutTest_Voter() throws IOException, ClassNotFoundException {
        server.startServer(null);
        when(server.getCommissionerService().isCommissioner(anyString())).thenReturn(false);
        when(server.getCandidateService().isCandidate(anyString())).thenReturn(false);
        server.logout(randomString());
        verify(server.getCommissionerService(), times(0)).logout(anyString());
        verify(server.getVoterService(), times(1)).logout(anyString());
        verify(server.getCandidateService(), times(0)).logout(anyString());
        server.stopServer(null);
    }

    @Test
    public void getVoterListTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.getVoterList(randomString());
        verify(server.getVoterService(), times(1)).getAll(anyString());
        server.stopServer(null);
    }

    @Test
    public void addCandidateTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.addCandidate(randomString());
        verify(server.getCandidateService(), times(1)).addCandidate(anyString());
        server.stopServer(null);
    }

    @Test
    public void confirmationCandidacyTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.confirmationCandidacy(randomString());
        verify(server.getCandidateService(), times(1)).confirmationCandidacy(anyString());
        server.stopServer(null);
    }

    @Test
    public void withdrawCandidacyTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.withdrawCandidacy(randomString());
        verify(server.getCandidateService(), times(1)).withdrawCandidacy(anyString());
        server.stopServer(null);
    }

    @Test
    public void addIdeaTest_Candidate() throws IOException, ClassNotFoundException {
        server.startServer(null);
        when(server.getCandidateService().isCandidate(anyString())).thenReturn(true);
        server.addIdea(randomString());
        verify(server.getCandidateService(), times(1)).addIdea(anyString());
        verify(server.getIdeaService(), times(0)).addIdea(anyString());
        server.stopServer(null);
    }

    @Test
    public void addIdeaTest_Voter() throws IOException, ClassNotFoundException {
        server.startServer(null);
        when(server.getCandidateService().isCandidate(anyString())).thenReturn(false);
        server.addIdea(randomString());
        verify(server.getCandidateService(), times(0)).addIdea(anyString());
        verify(server.getIdeaService(), times(1)).addIdea(anyString());
        server.stopServer(null);
    }

    @Test
    public void estimateIdeaTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.estimateIdea(randomString());
        verify(server.getIdeaService(), times(1)).estimate(anyString());
        server.stopServer(null);
    }

    @Test
    public void changeRatingTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.changeRating(randomString());
        verify(server.getIdeaService(), times(1)).changeRating(anyString());
        server.stopServer(null);
    }

    @Test
    public void removeRatingTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.removeRating(randomString());
        verify(server.getIdeaService(), times(1)).removeRating(anyString());
        server.stopServer(null);
    }

    @Test
    public void takeIdeaTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.takeIdea(randomString());
        verify(server.getCandidateService(), times(1)).addIdea(anyString());
        server.stopServer(null);
    }

    @Test
    public void removeIdeaTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.removeIdea(randomString());
        verify(server.getCandidateService(), times(1)).removeIdea(anyString());
        server.stopServer(null);
    }

    @Test
    public void getCandidateMapTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.getCandidatesMap(randomString());
        verify(server.getCandidateService(), times(1)).getCandidateMap(anyString());
        server.stopServer(null);
    }

    @Test
    public void getAllIdeasTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.getAllIdeas(randomString());
        verify(server.getIdeaService(), times(1)).getIdeas(anyString());
        server.stopServer(null);
    }

    @Test
    public void getAllVotersIdeasTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.getAllVotersIdeas(randomString());
        verify(server.getIdeaService(), times(1)).getAllVotersIdeas(anyString());
        server.stopServer(null);
    }

    @Test
    public void startElectionTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.startElection(randomString());
        verify(server.getCommissionerService(), times(1)).startElection(anyString());
        server.stopServer(null);
    }

    @Test
    public void voteTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.vote(randomString());
        verify(server.getElectionService(), times(1)).vote(anyString());
        server.stopServer(null);
    }

    @Test
    public void getElectionResultTest_Success() throws IOException, ClassNotFoundException {
        server.startServer(null);
        server.getElectionResult(randomString());
        verify(server.getCommissionerService(), times(1)).getElectionResult(anyString());
        server.stopServer(null);
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


    private class MyServer extends Server {
        public void startServer(String savedDataFileName) {
            gson = new Gson();
            sessionService = new SessionService(gson);
            contextService = new ContextService();
            ideaService = new IdeaService(contextService, gson, sessionService);
            voterService = new VoterService(sessionService, contextService, gson, ideaService);
            candidateService = new CandidateService(contextService, gson, sessionService, voterService, ideaService);
            electionService = new ElectionService(contextService, gson, sessionService, candidateService);
            commissionerService = new CommissionerService(sessionService, electionService,
                    contextService, gson, candidateService);
        }
    }
}