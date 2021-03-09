package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dto.request.*;
import net.thumbtack.school.elections.server.dto.response.ErrorDtoResponse;
import net.thumbtack.school.elections.server.dto.response.GetCandidateDtoResponse;
import net.thumbtack.school.elections.server.dto.response.GetElectionResultDtoResponse;
import net.thumbtack.school.elections.server.dto.response.GetVoterDtoResponse;
import net.thumbtack.school.elections.server.model.Candidate;
import net.thumbtack.school.elections.server.model.Voter;
import java.io.Serializable;
import java.util.*;

public class ElectionService implements Serializable {
    private final transient ContextService contextService;
    private final transient SessionService sessionService;
    private final transient CandidateService candidateService;
    private final transient Gson gson;
    private final transient Validation validation;
    private Map<Candidate, List<Voter>> candidateMap;
    private List<Voter> vsEveryone;

    private static final transient String EMPTY_JSON = "";
    private static final transient String NULL_VALUE = "Некорректный запрос.";

    public ElectionService(ContextService contextService, Gson gson, SessionService sessionService, CandidateService candidateService) {
        this.contextService = contextService;
        this.gson = gson;
        this.sessionService = sessionService;
        this.candidateService = candidateService;
        validation = new Validation();
    }

    /**
     * Start election, set "is election start" - true, create new candidate's map with candidates and their voters,
     * filling with the passed values, create new empty versus everyone set.
     * @param candidateSet set of candidates who have confirmed their candidacy.
     */
    public String startElection(String requestJsonString) {
        CommissionerStartElectionDtoRequest request =
                gson.fromJson(requestJsonString, CommissionerStartElectionDtoRequest.class);
        contextService.setIsElectionStart(gson.toJson(new SetIsElectionStartDtoRequest(true)));
        candidateMap = new HashMap<>();
        vsEveryone = new ArrayList<>();
        for (Candidate candidate : request.getCandidateSet()) {
            candidateMap.put(candidate, new ArrayList<>());
        }
        return EMPTY_JSON;
    }

    /**
     * If the voter did not contains in candidate's map and list versus everyone and the candidate is not equal null:
     * in the value of candidate from candidate's map put this voter.
     * If the voter did not contains in candidate's map and list versus everyone the candidate is equal null:
     * versus everyone list add this voter.
     * @param voter voter who wand vote.
     * @param candidate candidate to vote for.
     * @throws ServerException if election not start or election already stop.
     */
    public String vote(String requestJsonString) {
        if (!contextService.isElectionStart()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_NOT_START.getMessage()));
        }
        if (contextService.isElectionStop()) {
            return gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.ELECTION_STOP.getMessage()));
        }
        VoteDtoRequest request = gson.fromJson(requestJsonString, VoteDtoRequest.class);
        try {
            validation.validate(request.getToken());
            GetVoterDtoResponse getVoterDtoResponse = gson.fromJson(sessionService.getVoter(gson.toJson(
                    new GetVoterDtoRequest(request.getToken()))), GetVoterDtoResponse.class);
            Voter voter = getVoterDtoResponse.getVoter();
            if (candidateMap.values().stream()
                    .noneMatch(voters -> voters.contains(voter)) && vsEveryone.stream()
                    .noneMatch(voter1 -> voter1.equals(voter))) {
                if (request.getCandidateLogin() == null) {
                    vsEveryone.add(voter);
                } else if (!voter.getLogin().equals(request.getCandidateLogin())) {
                    Candidate candidate = gson.fromJson(candidateService.getCandidate(request.getCandidateLogin()), GetCandidateDtoResponse.class).getCandidate();
                    candidateMap.get(candidate).add(voter);
                }
            }
            return EMPTY_JSON;
        } catch (ServerException ex) {
            return gson.toJson(new ErrorDtoResponse(ex.getLocalizedMessage()));
        } catch (NullPointerException ignored) {
            return gson.toJson(new ErrorDtoResponse(NULL_VALUE));
        }
    }

    /**
     * Get election's result.
     * If the return value contains 2 or more elements, clear candidates map and versus everyone map,
     * fills the candidates map with new values, conducts the second round.
     * @return If the size of voters of at map one candidate is greater than the size of list versus everyone size:
     * set of candidates with the highest number of votes.
     * Else: null.
     */
    public String getElectionResult() {
        Set<Candidate> candidateSet = new HashSet<>();
        Candidate candidate = null;
        int i = 0;
        for (Map.Entry<Candidate, List<Voter>> entry: candidateMap.entrySet()) {
            if (entry.getValue().size() > i && entry.getValue().size() > vsEveryone.size()) {
                i = entry.getValue().size();
                candidate = entry.getKey();
            }
        }
        candidateSet.add(candidate);
        if (candidate != null) {
            for (Map.Entry<Candidate, List<Voter>> entry : candidateMap.entrySet()) {
                if (entry.getValue().size() == candidateMap.get(candidate).size() && !entry.getValue().equals(candidateMap.get(candidate))) {
                    candidateSet.add(entry.getKey());
                }
            }
            if (candidateSet.size() > 1) {
                startElection(gson.toJson(new CommissionerStartElectionDtoRequest(candidateSet)));
            } else {
                contextService.setIsElectionStop(gson.toJson(new SetIsElectionStopDtoRequest(true)));
            }
        } else {
            contextService.setIsElectionStop(gson.toJson(new SetIsElectionStopDtoRequest(true)));
        }
        return gson.toJson(new GetElectionResultDtoResponse(candidateSet));
    }

    public ContextService getContextService() {
        return contextService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public CandidateService getCandidateService() {
        return candidateService;
    }

    public void setCandidateMap(Map<Candidate, List<Voter>> candidateMap) {
        this.candidateMap = candidateMap;
    }

    public void setVsEveryone(List<Voter> vsEveryone) {
        this.vsEveryone = vsEveryone;
    }

    public Map<Candidate, List<Voter>> getCandidateMap() {
        return candidateMap;
    }

    public List<Voter> getVsEveryone() {
        return vsEveryone;
    }
}