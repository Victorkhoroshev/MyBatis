//package net.thumbtack.school.elections.server.service;
//import net.thumbtack.school.elections.server.Server;
//import net.thumbtack.school.elections.server.model.Candidate;
//import net.thumbtack.school.elections.server.model.Context;
//import net.thumbtack.school.elections.server.model.Voter;
//import org.junit.jupiter.api.Test;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TestContextService {
//    private final Server server = new Server();
//    @Test
//    public void syncTest() throws IOException, ClassNotFoundException {
//        server.startServer(null);
//        ContextService contextService = new ContextService();
//        Context context = contextService.getContext();
//        List<String> logins = new ArrayList<>();
//        Set<Voter> voterSet = new HashSet<>();
//        Set<Candidate> candidateSet = new HashSet<>();
//        logins.add("1");
//        Voter voter = new Voter("Виктор", "Хорошев",
//                "Пригородная", 21, 188, "victor@khoroshev.net"," 1111");
//        voterSet.add(voter);
//        candidateSet.add(new Candidate(voter));
//        context.setCandidateService(new CandidateService(contextService));
//        context.setIdeaService(new IdeaService(contextService));
//        context.setLogins(logins);
//        context.setVoterSet(voterSet);
//        context.setCandidateSet(candidateSet);
//        ContextService contextService1 = new ContextService(context);
//        contextService.sync();
//        assertAll(
//                () -> assertEquals(0, contextService1.getContext().getCandidateService().getCandidateMap().size()),
//                () -> assertEquals(0, contextService1.getContext().getIdeaService().getIdeas().size()),
//                () -> assertEquals(1, contextService1.getContext().getCandidateSet().size()),
//                () -> assertEquals(1, contextService1.getContext().getCandidateSet().size()),
//                () -> assertEquals(1, contextService1.getContext().getLogins().size())
//        );
//        ContextService contextService2 = new ContextService();
//        contextService2.sync();
//        assertAll(
//                () -> assertEquals(0, contextService2.getContext().getCandidateSet().size()),
//                () -> assertEquals(0, contextService2.getContext().getCandidateSet().size()),
//                () -> assertEquals(3, contextService2.getContext().getLogins().size())
//        );
//        server.stopServer(null);
//    }
//}