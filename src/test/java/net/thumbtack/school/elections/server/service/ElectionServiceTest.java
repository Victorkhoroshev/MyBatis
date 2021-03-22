package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.*;
import net.thumbtack.school.elections.server.exeption.ExceptionErrorCode;
import net.thumbtack.school.elections.server.exeption.ServerException;
import net.thumbtack.school.elections.server.model.Candidate;
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
public class ElectionServiceTest {
    @Mock
    private ContextService contextService;
    @Mock
    private SessionService sessionService;
    @Mock
    private CandidateService candidateService;
    private final Gson gson;
    private final ElectionService electionService;

    public ElectionServiceTest() {
        MockitoAnnotations.initMocks(this);
        gson = new Gson();
        electionService = new ElectionService(contextService, gson, sessionService, candidateService);
        electionService.setCandidateMap(new HashMap<>());
        electionService.setVsEveryone(new ArrayList<>());
    }

    @Test
    public void startElectionTest_Success() {
        Set<Candidate> candidateSet = new HashSet<>();
        candidateSet.add(new Candidate(getNewVoter()));
        candidateSet.add(new Candidate(getNewVoter()));
        assertEquals("", electionService.startElection(
                gson.toJson(new CommissionerStartElectionDtoRequest(candidateSet))));
    }

    @Test
    public void voteTest_Success() throws ServerException {
        when(electionService.getContextService().isElectionStart()).thenReturn(true);
        when(electionService.getContextService().isElectionStop()).thenReturn(false);
        Voter voter = getNewVoter();
        Candidate candidate = new Candidate(getNewVoter());
        electionService.getCandidateMap().put(candidate, new ArrayList<>());
        when(electionService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(electionService.getCandidateService().getCandidate(gson.toJson(new GetCandidateDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetCandidateDtoResponse(candidate)));
        assertEquals("", electionService.vote(gson.toJson(new VoteDtoRequest(randomString(), randomString()))));
        assertEquals(1, electionService.getCandidateMap().get(candidate).size());
        assertEquals(0, electionService.getVsEveryone().size());
    }

    @Test
    public void voteTest_Vs_Everyone() throws ServerException {
        when(electionService.getContextService().isElectionStart()).thenReturn(true);
        when(electionService.getContextService().isElectionStop()).thenReturn(false);
        Voter voter = getNewVoter();
        Candidate candidate = new Candidate(getNewVoter());
        electionService.getCandidateMap().put(candidate, new ArrayList<>());
        electionService.getCandidateMap().put(new Candidate(getNewVoter()), new ArrayList<>());
        when(electionService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(electionService.getCandidateService().getCandidate(gson.toJson(new GetCandidateDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetCandidateDtoResponse(candidate)));
        assertEquals("", electionService.vote(gson.toJson(new VoteDtoRequest(randomString(), null))));
        assertEquals(0, electionService.getCandidateMap().get(candidate).size());
        assertEquals(1, electionService.getVsEveryone().size());
    }

    @Test
    public void voteTest_Already_Vote_Vs_Everyone() throws ServerException {
        when(electionService.getContextService().isElectionStart()).thenReturn(true);
        when(electionService.getContextService().isElectionStop()).thenReturn(false);
        Voter voter = getNewVoter();
        Candidate candidate = new Candidate(getNewVoter());
        electionService.getCandidateMap().put(candidate, new ArrayList<>());
        electionService.getVsEveryone().add(voter);
        when(electionService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(electionService.getCandidateService().getCandidate(gson.toJson(new GetCandidateDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetCandidateDtoResponse(candidate)));
        assertEquals("", electionService.vote(gson.toJson(new VoteDtoRequest(randomString(), randomString()))));
        assertEquals(0, electionService.getCandidateMap().get(candidate).size());
        assertEquals(1, electionService.getVsEveryone().size());
    }

    @Test
    public void voteTest_Already_Vote() throws ServerException {
        when(electionService.getContextService().isElectionStart()).thenReturn(true);
        when(electionService.getContextService().isElectionStop()).thenReturn(false);
        Voter voter = getNewVoter();
        Candidate candidate = new Candidate(getNewVoter());
        electionService.getCandidateMap().put(candidate, new ArrayList<>());
        electionService.getCandidateMap().get(candidate).add(voter);
        when(electionService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(electionService.getCandidateService().getCandidate(gson.toJson(new GetCandidateDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetCandidateDtoResponse(candidate)));
        assertEquals("", electionService.vote(gson.toJson(new VoteDtoRequest(randomString(), randomString()))));
        assertEquals(1, electionService.getCandidateMap().get(candidate).size());
        assertEquals(0, electionService.getVsEveryone().size());
    }

    @Test
    public void voteTest_Candidate_Want_Vote_Yourself() throws ServerException {
        when(electionService.getContextService().isElectionStart()).thenReturn(true);
        when(electionService.getContextService().isElectionStop()).thenReturn(false);
        Voter voter = getNewVoter();
        Candidate candidate = new Candidate(voter);
        electionService.getCandidateMap().put(candidate, new ArrayList<>());
        when(electionService.getSessionService().getVoter(gson.toJson(new GetVoterDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetVoterDtoResponse(voter)));
        when(electionService.getCandidateService().getCandidate(gson.toJson(new GetCandidateDtoRequest(anyString()))))
                .thenReturn(gson.toJson(new GetCandidateDtoResponse(candidate)));
        assertEquals("", electionService.vote(gson.toJson(new VoteDtoRequest(randomString(), voter.getLogin()))));
        assertEquals(0, electionService.getCandidateMap().get(candidate).size());
        assertEquals(0, electionService.getVsEveryone().size());
    }

    @Test
    public void voteTest_Field_Is_Not_Valid() throws ServerException {
        when(electionService.getContextService().isElectionStart()).thenReturn(true);
        when(electionService.getContextService().isElectionStop()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                electionService.vote(gson.toJson(new VoteDtoRequest(null, randomString()))));
        verify(electionService.getSessionService(), times(0)).getVoter(anyString());
        verify(electionService.getCandidateService(), times(0)).getCandidate(anyString());
    }


    @Test
    public void voteTest_Json_Is_Null() throws ServerException {
        when(electionService.getContextService().isElectionStart()).thenReturn(true);
        when(electionService.getContextService().isElectionStop()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NULL_VALUE.getMessage())),
                electionService.vote(null));
        verify(electionService.getSessionService(), times(0)).getVoter(anyString());
        verify(electionService.getCandidateService(), times(0)).getCandidate(anyString());
    }

    @Test
    public void voteTest_Election_Not_Start() {
        when(electionService.getContextService().isElectionStart()).thenReturn(false);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_NOT_START.getMessage())),
                electionService.vote(randomString()));
    }

    @Test
    public void voteTest_Election_Already_Stop() {
        when(electionService.getContextService().isElectionStart()).thenReturn(true);
        when(electionService.getContextService().isElectionStop()).thenReturn(true);
        assertEquals(gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_STOP.getMessage())),
                electionService.vote(randomString()));
    }

    @Test
    public void getElectionResultTest_Success() {
        Candidate candidate1 = new Candidate(getNewVoter());
        Candidate candidate2 = new Candidate(getNewVoter());
        Candidate candidate3 = new Candidate(getNewVoter());
        Set<Candidate> candidateSet = new HashSet<>();
        candidateSet.add(candidate1);
        Voter voter1 = getNewVoter();
        Voter voter2 = getNewVoter();
        Voter voter3 = getNewVoter();
        Voter voter4 = getNewVoter();
        electionService.setCandidateMap(new HashMap<>());
        electionService.setVsEveryone(new ArrayList<>());
        electionService.getCandidateMap().put(candidate1, new ArrayList<>());
        electionService.getCandidateMap().put(candidate2, new ArrayList<>());
        electionService.getCandidateMap().put(candidate3, new ArrayList<>());
        electionService.getCandidateMap().get(candidate1).add(voter1);
        electionService.getCandidateMap().get(candidate1).add(voter2);
        electionService.getCandidateMap().get(candidate2).add(voter3);
        electionService.getVsEveryone().add(voter4);
        when(electionService.getContextService().setIsElectionStop(anyString()))
                .thenReturn(gson.toJson(new SetIsElectionStopDtoResponse(true)));
        assertEquals(gson.toJson(new GetElectionResultDtoResponse(candidateSet)), electionService.getElectionResult());
        verify(electionService.getContextService(), times(1)).setIsElectionStop(anyString());
    }

    @Test
    public void getElectionResultTest_Vs_Everyone_Vin() {
        Candidate candidate1 = new Candidate(getNewVoter());
        Candidate candidate2 = new Candidate(getNewVoter());
        Candidate candidate3 = new Candidate(getNewVoter());
        Set<Candidate> candidateSet = new HashSet<>();
        candidateSet.add(null);
        Voter voter1 = getNewVoter();
        Voter voter2 = getNewVoter();
        Voter voter3 = getNewVoter();
        Voter voter4 = getNewVoter();
        electionService.setCandidateMap(new HashMap<>());
        electionService.setVsEveryone(new ArrayList<>());
        electionService.getCandidateMap().put(candidate1, new ArrayList<>());
        electionService.getCandidateMap().put(candidate2, new ArrayList<>());
        electionService.getCandidateMap().put(candidate3, new ArrayList<>());
        electionService.getCandidateMap().get(candidate1).add(voter1);
        electionService.getCandidateMap().get(candidate2).add(voter2);
        electionService.getVsEveryone().add(voter3);
        electionService.getVsEveryone().add(voter4);
        when(electionService.getContextService().setIsElectionStop(anyString()))
                .thenReturn(gson.toJson(new SetIsElectionStopDtoResponse(true)));
        assertEquals(gson.toJson(new GetElectionResultDtoResponse(candidateSet)), electionService.getElectionResult());
        verify(electionService.getContextService(), times(1)).setIsElectionStop(anyString());
    }

    @Test
    public void getElectionResultTest_Two_Winners() {
        Candidate candidate1 = new Candidate(getNewVoter());
        Candidate candidate2 = new Candidate(getNewVoter());
        Candidate candidate3 = new Candidate(getNewVoter());
        Set<Candidate> candidateSet = new HashSet<>();
        candidateSet.add(candidate1);
        candidateSet.add(candidate2);
        Voter voter1 = getNewVoter();
        Voter voter2 = getNewVoter();
        Voter voter3 = getNewVoter();
        Voter voter4 = getNewVoter();
        Voter voter5 = getNewVoter();
        electionService.setCandidateMap(new HashMap<>());
        electionService.setVsEveryone(new ArrayList<>());
        electionService.getCandidateMap().put(candidate1, new ArrayList<>());
        electionService.getCandidateMap().put(candidate2, new ArrayList<>());
        electionService.getCandidateMap().put(candidate3, new ArrayList<>());
        electionService.getCandidateMap().get(candidate1).add(voter1);
        electionService.getCandidateMap().get(candidate1).add(voter2);
        electionService.getCandidateMap().get(candidate2).add(voter3);
        electionService.getCandidateMap().get(candidate2).add(voter4);
        electionService.getVsEveryone().add(voter5);
        when(electionService.getContextService().setIsElectionStop(anyString()))
                .thenReturn(gson.toJson(new SetIsElectionStopDtoResponse(true)));
        assertEquals(gson.toJson(new GetElectionResultDtoResponse(candidateSet)), electionService.getElectionResult());
        verify(electionService.getContextService(), times(0)).setIsElectionStop(anyString());
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