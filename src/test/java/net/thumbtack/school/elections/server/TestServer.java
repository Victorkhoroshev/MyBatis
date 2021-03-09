//package net.thumbtack.school.elections.server;
//
//import com.google.gson.Gson;
//import net.thumbtack.school.elections.server.database.Database;
//import net.thumbtack.school.elections.server.dto.request.*;
//import net.thumbtack.school.elections.server.dto.response.*;
//import net.thumbtack.school.elections.server.model.Candidate;
//import net.thumbtack.school.elections.server.model.Commissioner;
//import net.thumbtack.school.elections.server.model.Idea;
//import net.thumbtack.school.elections.server.model.Voter;
//import net.thumbtack.school.elections.server.service.*;
//import org.junit.jupiter.api.Test;
//import java.io.IOException;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TestServer {
//    private final Gson gson = new Gson();
//    private final Server server = new Server();
//    private final Commissioner commissioner = new Commissioner("victor.net", "25345Qw&&", true);
//    private static final String EMPTY_JSON = "";
//    private static final String NULL_VALUE = "Некорректный запрос.";
//    private static final String SESSION_NOT_FOUND = "Сессия пользователя не найдена.";
//    private static final String NULL_REGISTERVOTERDTOREQUEST = "Пожалуйста, заполните все данные.";
//    private static final String NULL_LOGINDTOREQUEST = "Пожалуйста, введите логин и пароль.";
//    private static final String WRONG_FIRSTNAME =
//            "Имя должно быть на кириллице, без пробелов, спец. символов и цифр.\n";
//    private static final String WRONG_LASTNAME =
//            "Фамилия должна быть на кириллице, без пробелов, спец. символов и цифр.\n";
//    private static final String WRONG_PATRONYMIC =
//            "Отчество должно быть на кириллице, без пробелов, спец. символов и цифр.\n";
//    private static final String WRONG_STREET =
//            "Название улицы должно быть на кириллице, без пробелов, спец. символов и цифр.\n";
//    private static final String WRONG_HOUSE = "Номер дома не может быть меньше единицы.\n";
//    private static final String WRONG_APARTMENT = "Номер квартиры не может быть меньше нуля.\n";
//    private static final String WRONG_LOGIN = "Длинна логина должна быть не меньше 9 символо.\n";
//    private static final String WRONG_PASSWORD =
//            "Пароль должен содержать хотя бы одну заглавную букву, одну строчную букву," +
//            " цифру и один спец. символ, а его длинна не менее 9 символов, без пробелов.\n";
//    private static final String ELECTION_START = "Выборы уже проходят, действие невозможно.";
//    @Test
//    public void startStopServerTest() throws IOException, ClassNotFoundException {
//        server.startServer(null);
//        assertAll(
//                () -> assertNotNull(server.gson),
//                () -> assertNotNull(server.contextService),
//                () -> assertNotNull(server.sessionService),
//                () -> assertNotNull(server.ideaService),
//                () -> assertNotNull(server.candidateService),
//                () -> assertNotNull(server.voterService),
//                () -> assertNotNull(server.electionService),
//                () -> assertNotNull(server.commissionerService)
//        );
//        server.stopServer(null);
//        assertAll(
//                () -> assertNull(server.gson),
//                () -> assertNull(server.contextService),
//                () -> assertNull(server.sessionService),
//                () -> assertNull(server.ideaService),
//                () -> assertNull(server.candidateService),
//                () -> assertNull(server.voterService),
//                () -> assertNull(server.commissionerService),
//                () -> assertNull(server.electionService)
//        );
//        server.startServer(null);
//        boolean isElectionStart = server.contextService.isElectionStart();
//        Set<Candidate> candidateSet = Database.getCandidateSet();
//        Set<Voter> voterSet = Database.getVoterSet();
//        List<String> logins = Database.getLogins();
//        CandidateService candidateService = server.candidateService;
//        IdeaService ideaService = server.ideaService;
//        server.stopServer(
//                "C:\\Thumbtack\\thumbtack_online_school_2020_2__viktor_khoroshev\\saveDataFile");
//        server.startServer(
//                "C:\\Thumbtack\\thumbtack_online_school_2020_2__viktor_khoroshev\\saveDataFile");
//        assertAll(
//                () -> assertEquals(candidateSet, Database.getCandidateSet()),
//                () -> assertEquals(voterSet, Database.getVoterSet()),
//                () -> assertEquals(logins, Database.getLogins()),
//                () -> assertEquals(candidateService, server.candidateService),
//                () -> assertEquals(ideaService, server.ideaService),
//                () -> assertEquals(isElectionStart, server.contextService.isElectionStart())
//        );
//    }
//
//    @Test
//    public void registerTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        SessionService session = server.sessionService;
//        RegisterDtoRequest request1 = new RegisterDtoRequest("Виктор", "Хорошев",
//                null,"Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request2 = new RegisterDtoRequest("викторр", "Хорошев",
//                null, "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request3 = new RegisterDtoRequest("ВИКТОРРР", "Хорошев",
//                null, "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request4 = new RegisterDtoRequest("victor", "Хорошев",
//                null,  "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request5 = new RegisterDtoRequest("Вик тор", "Хорошев",
//                null, "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request6 = new RegisterDtoRequest("Вик/тор", "Хорошев",
//                null,  "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request7 = new RegisterDtoRequest("Вик1тор", "Хорошев",
//                null,  "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request8 = new RegisterDtoRequest("Андрей", "Хорошев",
//                null, "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request9 = new RegisterDtoRequest("Андрей", "хорошевв",
//                null,  "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request10 = new RegisterDtoRequest("Аддрей", "ХОРОШЕВВВ",
//                null,  "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request11 = new RegisterDtoRequest("Аддрей", "Khoroshev",
//                null,  "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request12 = new RegisterDtoRequest("Аддрей", "ХОРО ШЕВ",
//                null,   "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request13 = new RegisterDtoRequest("Аддрей", "ХОРО.ЕВВВ",
//                null,  "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request14 = new RegisterDtoRequest("Аддрей", "ХОРО&ЕВВВ",
//                null,  "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request15 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request16 = new RegisterDtoRequest(randomString(), "Хорошев","игоревич",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request17 = new RegisterDtoRequest(randomString(), "Хорошев","ИГОРЕВИЧ",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request18 = new RegisterDtoRequest(randomString(), "Хорошев","Igorevich",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request19 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич1",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request20 = new RegisterDtoRequest(randomString(), "Хорошев","Игорев/ич",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request21 = new RegisterDtoRequest(randomString(), "Хорошев","Игорев ич",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request22 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request23 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request24 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "ПРИГОРОДНАЯ", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request25 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Prigiridnaya", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request26 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригор1одная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request27 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Приг ородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request28 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригор*одная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request29 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request30 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 0, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request31 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", -13, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request32 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest request33 = new RegisterDtoRequest(randomString(), "Хорошев",
//                "Игоревич", "Пригородная", 1, 1, randomString(), "111%111Aa");
//        RegisterDtoRequest request34 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, -1, randomString(), "111%111Aa");
//        RegisterDtoRequest request35 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, "vic", "111%111Aa");
//        RegisterDtoRequest request36 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111%111qQ");
//        RegisterDtoRequest request37 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111%111*гЖ");
//        RegisterDtoRequest request38 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "1г11%111Q");
//        RegisterDtoRequest request39 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111q%11Г1");
//        RegisterDtoRequest request40 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111111Aa");
//        RegisterDtoRequest request41 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "фффф%ффффAa");
//        RegisterDtoRequest request42 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111%111фa");
//        RegisterDtoRequest request43 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), " 111%111AФ");
//        RegisterDtoRequest request44 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "111%111Aa ");
//        RegisterDtoRequest request45 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "1qQ&йЕ");
//        RegisterDtoRequest request46 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "11%?AaфQ");
//        RegisterDtoRequest request47 = new RegisterDtoRequest(null, "Хорошев","Игоревич",
//                "Пригородная", 1, 188, randomString(), "11%?AaфQ");
//        RegisterDtoRequest request48 = new RegisterDtoRequest(randomString(), null,"Игоревич",
//                "Пригородная", 1, 188, randomString(), "11%?AaфQ");
//        RegisterDtoRequest request49 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                null, 1, 188, randomString(), "11%?AaфQ");
//        RegisterDtoRequest request50 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", 1, 188, null, "11%?AaфQ");
//        RegisterDtoRequest request51 = new RegisterDtoRequest(randomString(), "Хорошев","Игоревич",
//                "Пригородная", null, 188, randomString(), null);
//        RegisterDtoRequest request52 = new RegisterDtoRequest(null, null,null,
//                null, null, null, null, null);
//        RegisterDtoRequest request53 = new RegisterDtoRequest("V", "CD","POL",
//                "-ап", -1, -234, "null", "1");
//        assertAll(
//                () -> assertEquals(server.register(gson.toJson(request1)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request1.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request2)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request2.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request3)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request3.newVoter()).getToken()))),
//                () -> assertEquals(server.register( gson.toJson(request4)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_FIRSTNAME))),
//                () -> assertEquals(server.register(gson.toJson(request5)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_FIRSTNAME))),
//                () -> assertEquals(server.register(gson.toJson(request6)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_FIRSTNAME))),
//                () -> assertEquals(server.register(gson.toJson(request7)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_FIRSTNAME))),
//                () -> assertEquals(server.register(gson.toJson(request8)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request8.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request9)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request9.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request10)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request10.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request11)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_LASTNAME))),
//                () -> assertEquals(server.register(gson.toJson(request12)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_LASTNAME))),
//                () -> assertEquals(server.register(gson.toJson(request13)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_LASTNAME))),
//                () -> assertEquals(server.register(gson.toJson(request14)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_LASTNAME))),
//                () -> assertEquals(server.register(gson.toJson(request15)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request15.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request16)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request16.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request17)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request17.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request18)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PATRONYMIC))),
//                () -> assertEquals(server.register(gson.toJson(request19)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PATRONYMIC))),
//                () -> assertEquals(server.register(gson.toJson(request20)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PATRONYMIC))),
//                () -> assertEquals(server.register(gson.toJson(request21)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PATRONYMIC))),
//                () -> assertEquals(server.register(gson.toJson(request22)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request22.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request23)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request23.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request24)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request24.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request25)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_STREET))),
//                () -> assertEquals(server.register(gson.toJson(request26)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_STREET))),
//                () -> assertEquals(server.register(gson.toJson(request27)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_STREET))),
//                () -> assertEquals(server.register(gson.toJson(request28)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_STREET))),
//                () -> assertEquals(server.register(gson.toJson(request29)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request29.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request30)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_HOUSE))),
//                () -> assertEquals(server.register(gson.toJson(request31)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_HOUSE))),
//                () -> assertEquals(server.register(gson.toJson(request32)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request32.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request33)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request33.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request34)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_APARTMENT))),
//                () -> assertEquals(server.register(gson.toJson(request35)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_LOGIN))),
//                () -> assertEquals(server.register(gson.toJson(request36)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request36.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request36)),
//                        gson.toJson(new ErrorDtoResponse("Вы уже зарегестрированны."))),
//                () -> assertEquals(server.register(gson.toJson(request37)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request37.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request38)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request38.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request39)),
//                        gson.toJson(new RegisterDtoResponse(session.getSession(request39.newVoter()).getToken()))),
//                () -> assertEquals(server.register(gson.toJson(request40)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PASSWORD))),
//                () -> assertEquals(server.register(gson.toJson(request41)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PASSWORD))),
//                () -> assertEquals(server.register(gson.toJson(request42)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PASSWORD))),
//                () -> assertEquals(server.register(gson.toJson(request43)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PASSWORD))),
//                () -> assertEquals(server.register(gson.toJson(request44)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PASSWORD))),
//                () -> assertEquals(server.register(gson.toJson(request45)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PASSWORD))),
//                () -> assertEquals(server.register(gson.toJson(request46)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PASSWORD))),
//                () -> assertEquals(server.register(gson.toJson(request47)),
//                        gson.toJson(new ErrorDtoResponse(NULL_REGISTERVOTERDTOREQUEST))),
//                () -> assertEquals(server.register(gson.toJson(request48)),
//                        gson.toJson(new ErrorDtoResponse(NULL_REGISTERVOTERDTOREQUEST))),
//                () -> assertEquals(server.register(gson.toJson(request49)),
//                        gson.toJson(new ErrorDtoResponse(NULL_REGISTERVOTERDTOREQUEST))),
//                () -> assertEquals(server.register(gson.toJson(request50)),
//                        gson.toJson(new ErrorDtoResponse(NULL_REGISTERVOTERDTOREQUEST))),
//                () -> assertEquals(server.register(gson.toJson(request51)),
//                        gson.toJson(new ErrorDtoResponse(NULL_REGISTERVOTERDTOREQUEST))),
//                () -> assertEquals(server.register(gson.toJson(request52)),
//                        gson.toJson(new ErrorDtoResponse(NULL_REGISTERVOTERDTOREQUEST))),
//                () -> assertEquals(server.register(null),
//                        gson.toJson(new ErrorDtoResponse(NULL_REGISTERVOTERDTOREQUEST))),
//                () -> assertEquals(server.register(gson.toJson(request53)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_FIRSTNAME + WRONG_LASTNAME + WRONG_PATRONYMIC +
//                                WRONG_STREET + WRONG_HOUSE + WRONG_APARTMENT + WRONG_LOGIN + WRONG_PASSWORD)))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest = new StartElectionDtoRequest(
//                server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        RegisterDtoRequest request54 = new RegisterDtoRequest(randomString(), randomString(),
//                randomString(),randomString(),1, 1, "login111111", "1qQ&2$$$$$$$");
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)), server.register(gson.toJson(request54)));
//        server.startServer(null);
//    }
//    //TODO: МОККИТО
//    @Test
//    public void logoutTest() throws ServerException, IOException, ClassNotFoundException {
//        server.startServer(null);
//        SessionService session = server.sessionService;
//        LoginDtoRequest loginDtoRequest = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(loginDtoRequest));
//        RegisterDtoRequest registerDtoRequest = new RegisterDtoRequest(randomString(), randomString(),
//                null,randomString(), 1, 188, randomString(), "111%111Aa");
//        RegisterDtoRequest registerDtoRequest1 = new RegisterDtoRequest(randomString(), randomString(),
//                null,randomString(), 1, 188, randomString(), "111%111Aa");
//        server.register(gson.toJson(registerDtoRequest));
//        server.register(gson.toJson(registerDtoRequest1));
//        server.candidateService.confirmationCandidacy(registerDtoRequest1.newVoter(),new ArrayList<>());
//        LogoutDtoRequest request = new LogoutDtoRequest(session.getSession(registerDtoRequest.newVoter()).getToken());
//        LogoutDtoRequest request1 = null;
//        LogoutDtoRequest request2 = new LogoutDtoRequest(null);
//        LogoutDtoRequest request3 = new LogoutDtoRequest(randomString());
//        LogoutDtoRequest request4 = new LogoutDtoRequest(session.getSession(registerDtoRequest1.newVoter()).getToken());
//        LogoutDtoRequest request5 = new LogoutDtoRequest(session.getSession(commissioner).getToken());
//        assertAll(
//                () -> assertEquals(EMPTY_JSON, server.logout(gson.toJson(request))),
//                () -> assertEquals(EMPTY_JSON, server.logout(gson.toJson(request5))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(
//                        "Невозможно разлогиниться, для начала, снимите свою кандидатуру с выборов.")),
//                        server.logout(gson.toJson(request4))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)), server.logout(gson.toJson(request1))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)), server.logout(gson.toJson(request2))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.logout(gson.toJson(request3)))
//        );
//        server.stopServer(null);
//    }
//    @Test
//    public void loginTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        SessionService session = server.sessionService;
//        RegisterDtoRequest registerDtoRequest = new RegisterDtoRequest(randomString(), randomString(),
//                null,randomString(), 1, 188, "login1111111", "111%111Aa");
//        RegisterDtoRequest registerDtoRequest1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 188, "login222222", "111%111Aa");
//        RegisterDtoRequest registerDtoRequest2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 188, "login333333", "111%111Aa");
//        RegisterDtoRequest registerDtoRequest3 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 188, "login444444", "111%111Aa");
//        server.register(gson.toJson(registerDtoRequest));
//        server.register(gson.toJson(registerDtoRequest1));
//        server.register(gson.toJson(registerDtoRequest2));
//        server.register(gson.toJson(registerDtoRequest3));
//        LoginDtoRequest loginDtoRequest1 = new LoginDtoRequest("login1111111", "111%111Aa");
//        LoginDtoRequest loginDtoRequest2 = new LoginDtoRequest("login222222", "111%111Aa");
//        LoginDtoRequest loginDtoRequest3 = new LoginDtoRequest("login333333", "111%111Aa");
//        LoginDtoRequest loginDtoRequest4 = new LoginDtoRequest("login333333", "111%111Aa1");
//        LoginDtoRequest loginDtoRequest5 = new LoginDtoRequest("login333333", "11Aa1");
//        LoginDtoRequest loginDtoRequest6 = new LoginDtoRequest("login44444444444", "111%111Aa");
//        LoginDtoRequest loginDtoRequest7 = null;
//        LoginDtoRequest loginDtoRequest8 = new LoginDtoRequest(null, null);
//        LoginDtoRequest loginDtoRequest9 = new LoginDtoRequest("log", "111%111Aa");
//        assertAll(
//                () -> assertEquals(server.login(gson.toJson(loginDtoRequest1)),
//                        gson.toJson(new LoginDtoResponse(session.getSession(registerDtoRequest.newVoter()).getToken()))),
//                () -> assertEquals(server.login(gson.toJson(loginDtoRequest2)),
//                        gson.toJson(new LoginDtoResponse(session.getSession(registerDtoRequest1.newVoter()).getToken()))),
//                () -> assertEquals(server.login(gson.toJson(loginDtoRequest3)),
//                        gson.toJson(new LoginDtoResponse(session.getSession(registerDtoRequest2.newVoter()).getToken()))),
//                () -> assertEquals(server.login(gson.toJson(loginDtoRequest4)),
//                        gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.WRONG_PASSWORD.getMessage()))),
//                () -> assertEquals(server.login(gson.toJson(loginDtoRequest5)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_PASSWORD))),
//                () -> assertEquals(server.login(gson.toJson(loginDtoRequest6)),
//                        gson.toJson(new ErrorDtoResponse(ExceptionErrorCode.NOT_FOUND.getMessage()))),
//                () -> assertEquals(server.login(gson.toJson(loginDtoRequest7)),
//                        gson.toJson(new ErrorDtoResponse(NULL_LOGINDTOREQUEST))),
//                () -> assertEquals(server.login(gson.toJson(loginDtoRequest8)),
//                        gson.toJson(new ErrorDtoResponse(NULL_LOGINDTOREQUEST))),
//                () -> assertEquals(server.login(gson.toJson(loginDtoRequest9)),
//                        gson.toJson(new ErrorDtoResponse(WRONG_LOGIN)))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest =
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.login(gson.toJson(new LoginDtoRequest(registerDtoRequest3.newVoter().getLogin(),
//                        registerDtoRequest3.newVoter().getPassword()))));
//        server.stopServer(null);
//    }
//    @Test
//    public void getVoterListTest() throws IOException, ClassNotFoundException {
//        server.startServer(null);
//        RegisterDtoRequest registerDtoRequest = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest registerDtoRequest1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        RegisterDtoRequest registerDtoRequest2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber3", "111^wW1234");
//        server.register(gson.toJson(registerDtoRequest));
//        server.register(gson.toJson(registerDtoRequest1));
//        server.register(gson.toJson(registerDtoRequest2));
//        Set<Voter> voterSet = new HashSet<>();
//        voterSet.add(registerDtoRequest.newVoter());
//        voterSet.add(registerDtoRequest1.newVoter());
//        voterSet.add(registerDtoRequest2.newVoter());
//        assertAll(
//                () -> assertEquals(server.getVoterList(gson.toJson(
//                        new GetVoterListDtoRequest(server.sessionService.getSession(
//                                server.voterService.get("loginNumber1")).getToken()))).length(),
//                        gson.toJson(new GetVotersListDtoResponse(voterSet)).length()),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getVoterList(null)),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getVoterList(gson.toJson(new GetVoterListDtoRequest(null)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.getVoterList(gson.toJson(new GetVoterListDtoRequest("1"))))
//        );
//        server.stopServer(null);
//    }
//    @Test
//    public void addCandidateTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        RegisterDtoRequest request3 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber3", "111^wW1234");
//        RegisterDtoRequest request4 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber4", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        server.register(gson.toJson(request3));
//        server.register(gson.toJson(request4));
//        assertAll(
//                () -> assertEquals(EMPTY_JSON, server.addCandidate(gson.toJson(
//                        new AddCandidateDtoRequest(request2.newVoter().getLogin(),
//                                server.sessionService.getSession(request1.newVoter()).getToken())))),
//                () -> assertTrue(server.voterService.get("loginNumber1").isHasOwnCandidate()),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.addCandidate(gson.toJson(new AddCandidateDtoRequest(
//                                request3.newVoter().getLogin(), "1")))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.addCandidate(null)),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.addCandidate(gson.toJson(new AddCandidateDtoRequest(
//                                null, server.sessionService.getSession(request1.newVoter()).getToken())))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.addCandidate(gson.toJson(new AddCandidateDtoRequest(
//                                "loginNumber3" , null))))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest =
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.addCandidate(gson.toJson(new AddCandidateDtoRequest(
//                        request4.newVoter().getLogin(), server.sessionService.getSession(
//                                request4.newVoter()).getToken()))));
//        server.stopServer(null);
//    }
//    @Test
//    public void confirmationCandidacyTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        List<String> text1 = new ArrayList<>();
//        List<String> text2 = new ArrayList<>();
//        text1.add("idea №1");
//        text2.add("idea №1");
//        text1.add("idea №2");
//        text2.add("idea №2");
//        text1.add("idea №3");
//        text2.add("idea №3");
//        assertAll(
//                () -> assertEquals(EMPTY_JSON, server.confirmationCandidacy(gson.toJson(new
//                        ConfirmationCandidacyDtoRequest(server.sessionService.getSession(
//                                request1.newVoter()).getToken(), text1)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(
//                                "1", text1)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.confirmationCandidacy(null)),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.confirmationCandidacy(gson.toJson(
//                                new ConfirmationCandidacyDtoRequest(null, text1)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.confirmationCandidacy(gson.toJson(
//                                new ConfirmationCandidacyDtoRequest("1", null))))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest = new StartElectionDtoRequest(
//                server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(
//                        server.sessionService.getSession(request2.newVoter()).getToken(), text2))));
//        server.stopServer(null);
//    }
//    @Test
//    public void withdrawCandidacyTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        List<String> text1 = new ArrayList<>();
//        List<String> text2 = new ArrayList<>();
//        text1.add("idea №1");
//        text2.add("idea №1");
//        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(
//                server.sessionService.getSession(request1.newVoter()).getToken(), text1)));
//        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(
//                server.sessionService.getSession(request2.newVoter()).getToken(), text2)));
//        assertAll(
//                () -> assertEquals(EMPTY_JSON, server.withdrawCandidacy(gson.toJson(
//                        new WithdrawCandidacyRequest(server.sessionService.getSession(request1.newVoter()).getToken())))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.withdrawCandidacy(gson.toJson(new WithdrawCandidacyRequest("1")))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.withdrawCandidacy(null)),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.withdrawCandidacy(gson.toJson(new WithdrawCandidacyRequest(null))))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest =
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.withdrawCandidacy(gson.toJson(new WithdrawCandidacyRequest(
//                        server.sessionService.getSession(request2.newVoter()).getToken()))));
//        server.stopServer(null);
//    }
//    //TODO: МОККИТО
//    @Test
//    public void addIdeaTest() throws IOException, ServerException, ClassNotFoundException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        List<String> text = new ArrayList<>();
//        text.add("idea №1");
//        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(
//                server.sessionService.getSession(request2.newVoter()).getToken(), text)));
//        assertAll(
//                () -> assertEquals(EMPTY_JSON ,server.addIdea(gson.toJson(
//                        new AddIdeaDtoRequest("idea №2", server.sessionService.getSession(
//                                request1.newVoter()).getToken())))),
//                () -> assertEquals(EMPTY_JSON ,server.addIdea(gson.toJson(
//                        new AddIdeaDtoRequest("idea №3", server.sessionService.getSession(
//                                request2.newVoter()).getToken())))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.addIdea(gson.toJson(null))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.addIdea(gson.toJson(new AddIdeaDtoRequest(
//                                null, server.sessionService.getSession(request1.newVoter()).getToken())))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea №4", null)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea №5", "1"))))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest =
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.addIdea(gson.toJson(new AddIdeaDtoRequest(
//                        "idea №4", server.sessionService.getSession(request1.newVoter()).getToken()))));
//        server.stopServer(null);
//    }
//    @Test
//    public void estimateIdeaTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        RegisterDtoRequest request3 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber3", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        server.register(gson.toJson(request3));
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea №1",
//                server.sessionService.getSession(request1.newVoter()).getToken())));
//        assertAll(
//                () -> assertEquals(EMPTY_JSON, server.estimateIdea(gson.toJson(
//                        new EstimateIdeaDtoRequest(server.voterService.get("loginNumber1").getLogin(), 1,
//                                server.sessionService.getSession(request2.newVoter()).getToken())))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse("Оценка должна быть от 1 до 5.")),
//                        server.estimateIdea(gson.toJson(new EstimateIdeaDtoRequest(
//                                server.voterService.get("loginNumber1").getLogin(), 0,
//                                server.sessionService.getSession(request2.newVoter()).getToken())))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.estimateIdea(gson.toJson(null))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.estimateIdea(gson.toJson(new EstimateIdeaDtoRequest(
//                                null, 1, server.sessionService.getSession(request2.newVoter()).getToken())))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.estimateIdea(gson.toJson(new EstimateIdeaDtoRequest(
//                                server.voterService.get("loginNumber1").getLogin(), 1, null))))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest =
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.estimateIdea(gson.toJson(new EstimateIdeaDtoRequest(
//                        server.voterService.get("loginNumber1").getLogin(), 1,
//                        server.sessionService.getSession(request3.newVoter()).getToken()))));
//        server.stopServer(null);
//    }
//    @Test
//    public void changeRatingTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        String token1 = server.sessionService.getSession(request1.newVoter()).getToken();
//        String token2 = server.sessionService.getSession(request2.newVoter()).getToken();
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea №1", token1)));
//        server.estimateIdea(gson.toJson(
//                new EstimateIdeaDtoRequest(server.voterService.get("loginNumber1").getLogin(), 1, token2)));
//        assertAll(
//                () -> assertEquals(EMPTY_JSON, server.changeRating(gson.toJson(new ChangeRatingDtoRequest(token2,
//                        server.ideaService.getKey(server.sessionService.getVoter(token1), "idea №1"), 5)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse("Оценка должна быть от 1 до 5.")),
//                        server.changeRating(gson.toJson(new ChangeRatingDtoRequest(token2, server.ideaService.getKey(
//                                server.sessionService.getVoter(token1), "idea №1"), 0)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.changeRating(gson.toJson(null))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.changeRating(gson.toJson(new ChangeRatingDtoRequest(
//                                null, server.ideaService.getKey(server.sessionService.getVoter(token1),
//                                "idea №1"), 5)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.changeRating(gson.toJson(new ChangeRatingDtoRequest(token2, null, 5))))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest =
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.changeRating(gson.toJson(new ChangeRatingDtoRequest(token2,
//                        server.ideaService.getKey(server.sessionService.getVoter(token1), "idea №1"), 4))));
//        server.stopServer(null);
//    }
//    @Test
//    public void removeRatingTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        String token1 = server.sessionService.getSession(request1.newVoter()).getToken();
//        String token2 = server.sessionService.getSession(request2.newVoter()).getToken();
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea №1", token1)));
//        server.estimateIdea(gson.toJson(new EstimateIdeaDtoRequest(server.voterService.get("loginNumber1").getLogin(), 1, token2)));
//        assertAll(
//                () -> assertEquals(EMPTY_JSON, server.removeRating(gson.toJson(
//                        new RemoveRatingDtoRequest(token2,
//                                server.ideaService.getKey(server.sessionService.getVoter(token1), "idea №1"))))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.removeRating(gson.toJson(new RemoveRatingDtoRequest("1",
//                                server.ideaService.getKey(server.sessionService.getVoter(token1), "idea №1"))))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.removeRating(gson.toJson(null))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.removeRating(gson.toJson(new RemoveRatingDtoRequest(null,
//                                server.ideaService.getKey(server.sessionService.getVoter(token1), "idea №1"))))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.removeRating(gson.toJson(new RemoveRatingDtoRequest(token2, null))))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.estimateIdea(gson.toJson(
//                new EstimateIdeaDtoRequest(server.voterService.get("loginNumber1").getLogin(), 1, token2)));
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest =
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.removeRating(gson.toJson(new RemoveRatingDtoRequest(token2,
//                        server.ideaService.getKey(server.sessionService.getVoter(token1), "idea №1")))));
//        server.stopServer(null);
//    }
//    @Test
//    public void takeIdeaTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        String token1 = server.sessionService.getSession(request1.newVoter()).getToken();
//        String token2 = server.sessionService.getSession(request2.newVoter()).getToken();
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea №1", token1)));
//        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(token2, new ArrayList<>())));
//        assertAll(
//                () -> assertEquals(EMPTY_JSON, server.takeIdea(gson.toJson(
//                        new TakeIdeaDtoRequest(token2, server.ideaService.getKey(server.sessionService.getVoter(token1),
//                                "idea №1"))))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.takeIdea(gson.toJson(null))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.takeIdea(gson.toJson(new TakeIdeaDtoRequest(null,
//                                server.ideaService.getKey(server.sessionService.getVoter(token1), "idea №1"))))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.takeIdea(gson.toJson(new TakeIdeaDtoRequest("1",
//                                server.ideaService.getKey(server.sessionService.getVoter(token1), "idea №1"))))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.takeIdea(gson.toJson(new TakeIdeaDtoRequest(token2, null))))
//        );
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest =
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.takeIdea(gson.toJson(new TakeIdeaDtoRequest(token2,
//                        server.ideaService.getKey(server.sessionService.getVoter(token1), "idea №1")))));
//        server.stopServer(null);
//    }
//    @Test
//    public void removeIdeaTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        String token1 = server.sessionService.getSession(request1.newVoter()).getToken();
//        String token2 = server.sessionService.getSession(request2.newVoter()).getToken();
//        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(token1, new ArrayList<>())));
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 1", token2)));
//        String ideaKey = server.ideaService.getKey(server.sessionService.getVoter(token2), "idea 1");
//        server.takeIdea(gson.toJson(new TakeIdeaDtoRequest(token1, ideaKey)));
//        assertAll(
//                () -> assertEquals(EMPTY_JSON , server.removeIdea(gson.toJson(
//                        new RemoveIdeaDtoRequest(token1, ideaKey)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.removeIdea(gson.toJson(null))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.removeIdea(gson.toJson(new RemoveIdeaDtoRequest(null, ideaKey)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.removeIdea(gson.toJson(new RemoveIdeaDtoRequest("1", ideaKey)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.removeIdea(gson.toJson(new RemoveIdeaDtoRequest(token1, null))))
//        );
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 2", token2)));
//        String ideaKey1 = server.ideaService.getKey(server.sessionService.getVoter(token2), "idea 2");
//        LoginDtoRequest request = new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword());
//        server.login(gson.toJson(request));
//        StartElectionDtoRequest startRequest =
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken());
//        server.startElection(gson.toJson(startRequest));
//        assertEquals(gson.toJson(new ErrorDtoResponse(ELECTION_START)),
//                server.removeIdea(gson.toJson(new RemoveIdeaDtoRequest(token1, ideaKey1))));
//        server.stopServer(null);
//    }
//    @Test
//    public void getCandidateMapTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        RegisterDtoRequest request3 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber3", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        server.register(gson.toJson(request3));
//        String token1 = server.sessionService.getSession(request1.newVoter()).getToken();
//        String token2 = server.sessionService.getSession(request2.newVoter()).getToken();
//        String token3 = server.sessionService.getSession(request3.newVoter()).getToken();
//        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(token1, new ArrayList<>())));
//        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(token2, new ArrayList<>())));
//        assertAll(
//                () -> assertEquals(gson.toJson(
//                        new GetCandidateMapDtoResponse(server.candidateService.getCandidateMap())),
//                        server.getCandidatesMap(gson.toJson(new GetCandidateMapDtoRequest(token3)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.getCandidatesMap(gson.toJson(new GetCandidateMapDtoRequest("1")))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getCandidatesMap(null)),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getCandidatesMap(gson.toJson(new GetCandidateMapDtoRequest(null))))
//        );
//        server.stopServer(null);
//    }
//    @Test
//    public void getAllIdeasTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        String token1 = server.sessionService.getSession(request1.newVoter()).getToken();
//        String token2 = server.sessionService.getSession(request2.newVoter()).getToken();
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 1", token1)));
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 2", token1)));
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 3", token1)));
//        String ideaKey1 = server.ideaService.getKey(server.sessionService.getVoter(token1), "idea 1");
//        String ideaKey2 = server.ideaService.getKey(server.sessionService.getVoter(token1), "idea 2");
//        String ideaKey3 = server.ideaService.getKey(server.sessionService.getVoter(token1), "idea 3");
//        server.estimateIdea(gson.toJson(new EstimateIdeaDtoRequest(ideaKey1, 5, token2)));
//        server.estimateIdea(gson.toJson(new EstimateIdeaDtoRequest(ideaKey2, 3, token2)));
//        server.estimateIdea(gson.toJson(new EstimateIdeaDtoRequest(ideaKey3, 1, token2)));
//        Map<Idea, Float> map = new TreeMap<>();
//        map.put(server.ideaService.getIdea(ideaKey1), 5f);
//        map.put(server.ideaService.getIdea(ideaKey2), 4f);
//        map.put(server.ideaService.getIdea(ideaKey3), 3f);
//        assertAll(
//                () -> assertEquals(gson.toJson(new GetAllIdeasDtoResponse(map)),
//                        server.getAllIdeas(gson.toJson(new GetAllIdeasDtoRequest(token2)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.getAllIdeas(gson.toJson(new GetAllIdeasDtoRequest("token2")))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getAllIdeas(gson.toJson(null))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getAllIdeas(gson.toJson(new GetAllIdeasDtoRequest(null))))
//        );
//        server.stopServer(null);
//    }
//    @Test
//    public void getAllVotersIdeasTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest request1 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber1", "111^wW1234");
//        RegisterDtoRequest request2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 1, "loginNumber2", "111^wW1234");
//        server.register(gson.toJson(request1));
//        server.register(gson.toJson(request2));
//        String token1 = server.sessionService.getSession(request1.newVoter()).getToken();
//        String token2 = server.sessionService.getSession(request2.newVoter()).getToken();
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 1", token1)));
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 2", token1)));
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 3", token1)));
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 4", token2)));
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 5", token2)));
//        server.addIdea(gson.toJson(new AddIdeaDtoRequest("idea 6", token2)));
//        String ideaKey1 = server.ideaService.getKey(server.sessionService.getVoter(token1), "idea 1");
//        String ideaKey2 = server.ideaService.getKey(server.sessionService.getVoter(token1), "idea 2");
//        String ideaKey3 = server.ideaService.getKey(server.sessionService.getVoter(token1), "idea 3");
//        List<String> strings = new ArrayList<>();
//        strings.add(request1.newVoter().getLogin());
//        List<Idea> list = new ArrayList<>();
//        list.add(server.ideaService.getIdea(ideaKey1));
//        list.add(server.ideaService.getIdea(ideaKey2));
//        list.add(server.ideaService.getIdea(ideaKey3));
//        assertAll(
//                () -> assertEquals(gson.toJson(new GetAllVotersIdeasDtoResponse(list)),
//                        server.getAllVotersIdeas(gson.toJson(new GetAllVotersIdeasDtoRequest(token1, strings)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(SESSION_NOT_FOUND)),
//                        server.getAllVotersIdeas(gson.toJson(new GetAllVotersIdeasDtoRequest("token1", strings)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getAllVotersIdeas(gson.toJson(null))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getAllVotersIdeas(gson.toJson(new GetAllVotersIdeasDtoRequest(null, strings)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getAllVotersIdeas(
//                                gson.toJson(new GetAllVotersIdeasDtoRequest("token1", null))))
//        );
//        server.stopServer(null);
//    }
//    @Test
//    public void startElectionTest() throws IOException, ClassNotFoundException {
//        server.startServer(null);
//        assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                server.startElection(gson.toJson(new StartElectionDtoRequest(null))));
//        assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)), server.startElection(gson.toJson(null)));
//        assertEquals(gson.toJson(new ErrorDtoResponse("Вы не председатель коммиссии.")),
//                server.startElection(gson.toJson(new StartElectionDtoRequest("1"))));
//        server.stopServer(null);
//    }
//    @Test
//    public void voteTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest voter1 = new RegisterDtoRequest(randomString(), randomString(),
//                null,randomString(), 1, 188, "login1111111", "111%111Aa");
//        RegisterDtoRequest voter2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 188, "login222222", "111%111Aa");
//        RegisterDtoRequest voter3 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 188, "login333333", "111%111Aa");
//        RegisterDtoRequest voter4 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 188, "login444444", "111%111Aa");
//        server.register(gson.toJson(voter1));
//        server.register(gson.toJson(voter2));
//        server.register(gson.toJson(voter3));
//        server.register(gson.toJson(voter4));
//        server.confirmationCandidacy(gson.toJson(
//                new ConfirmationCandidacyDtoRequest(server.sessionService.getSession(voter1.newVoter()).getToken(),
//                        new ArrayList<>())));
//        server.confirmationCandidacy(gson.toJson(
//                new ConfirmationCandidacyDtoRequest(server.sessionService.getSession(voter2.newVoter()).getToken(),
//                        new ArrayList<>())));
//        assertEquals(gson.toJson(new ErrorDtoResponse("Голосование не началось.")),
//                server.vote(gson.toJson(new VoteDtoRequest(server.sessionService.getSession(voter3.newVoter()).getToken(),
//                        "login1111111"))));
//        server.login(gson.toJson(new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword())));
//        server.startElection(gson.toJson(
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken())));
//        assertAll(
//                () -> assertEquals(EMPTY_JSON, server.vote(gson.toJson(
//                        new VoteDtoRequest(server.sessionService.getSession(voter3.newVoter()).getToken(),
//                                "login1111111")))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse("Кандидат не найден.")),
//                        server.vote(gson.toJson(new VoteDtoRequest(
//                                server.sessionService.getSession(voter3.newVoter()).getToken(),
//                                "login11112111")))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.vote(gson.toJson(new VoteDtoRequest(null , "login1111111")))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.vote(gson.toJson(null)))
//        );
//        server.contextService.setIsElectionStop(true);
//        assertEquals(gson.toJson(new ErrorDtoResponse("Голосование закончилось.")),
//                server.vote(gson.toJson(
//                        new VoteDtoRequest(server.sessionService.getSession(voter4.newVoter()).getToken(),
//                                "login1111111"))));
//        server.stopServer(null);
//    }
//    @Test
//    public void getElectionResultTest() throws IOException, ClassNotFoundException, ServerException {
//        server.startServer(null);
//        RegisterDtoRequest registerDtoRequest1 = new RegisterDtoRequest(randomString(), randomString(),
//                null,randomString(), 1, 188, "login1111111", "111%111Aa");
//        RegisterDtoRequest registerDtoRequest2 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 188, "login222222", "111%111Aa");
//        RegisterDtoRequest registerDtoRequest3 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 188, "login333333", "111%111Aa");
//        RegisterDtoRequest registerDtoRequest4 = new RegisterDtoRequest(randomString(), randomString(),
//                null, randomString(), 1, 188, "login444444", "111%111Aa");
//        Voter voter1 = registerDtoRequest1.newVoter();
//        Voter voter2 = registerDtoRequest2.newVoter();
//        Voter voter3 = registerDtoRequest3.newVoter();
//        Voter voter4 = registerDtoRequest4.newVoter();
//        server.register(gson.toJson(registerDtoRequest1));
//        server.register(gson.toJson(registerDtoRequest2));
//        server.register(gson.toJson(registerDtoRequest3));
//        server.register(gson.toJson(registerDtoRequest4));
//        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(
//                server.sessionService.getSession(voter1).getToken(), new ArrayList<>())));
//        server.confirmationCandidacy(gson.toJson(new ConfirmationCandidacyDtoRequest(
//                server.sessionService.getSession(voter2).getToken(), new ArrayList<>())));
//        server.login(gson.toJson(new LoginDtoRequest(commissioner.getLogin(), commissioner.getPassword())));
//        server.startElection(gson.toJson(
//                new StartElectionDtoRequest(server.sessionService.getSession(commissioner).getToken())));
//        server.vote(gson.toJson(new VoteDtoRequest(server.sessionService.getSession(voter3).getToken(),
//                "login1111111")));
//        Set<Candidate> candidateSet = new HashSet<>();
//        voter1.setHasOwnCandidate(true);
//        candidateSet.add(new Candidate(voter1));
//        assertAll(
//                () -> assertEquals(gson.toJson(new GetElectionResultDtoResponse(candidateSet)),
//                        server.getElectionResult(gson.toJson(new GetElectionResultDtoRequest(
//                                server.sessionService.getSession(commissioner).getToken())))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse("Вы не председатель коммиссии.")),
//                        server.getElectionResult(gson.toJson(new GetElectionResultDtoRequest("1")))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getElectionResult(gson.toJson(new GetElectionResultDtoRequest(null)))),
//                () -> assertEquals(gson.toJson(new ErrorDtoResponse(NULL_VALUE)),
//                        server.getElectionResult(gson.toJson(null)))
//        );
//        server.stopServer(null);
//    }
//
//    private String randomString() {
//        Random random = new Random();
//        char[] sAlphabet = "АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзиклмнопрстуфхцчшщъыьэюя".toCharArray();
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < 60; i++) {
//            stringBuilder.append(sAlphabet[random.nextInt(sAlphabet.length)]);
//        }
//        return stringBuilder.toString();
//    }
//}
