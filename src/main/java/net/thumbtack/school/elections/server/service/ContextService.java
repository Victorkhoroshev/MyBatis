package net.thumbtack.school.elections.server.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.server.dao.ContextDao;
import net.thumbtack.school.elections.server.daoimpl.ContextDaoImpl;
import net.thumbtack.school.elections.server.dto.request.GetElectionResultDtoRequest;
import net.thumbtack.school.elections.server.dto.request.SetIsElectionStartDtoRequest;
import net.thumbtack.school.elections.server.dto.request.SetIsElectionStopDtoRequest;
import net.thumbtack.school.elections.server.dto.response.SetIsElectionStartDtoResponse;
import net.thumbtack.school.elections.server.dto.response.SetIsElectionStopDtoResponse;
import net.thumbtack.school.elections.server.model.Context;

public class ContextService {
    private final Context context;
    private final ContextDao contextDao;
    private Gson gson;

    public ContextService() {
        context = Context.getInstance();
        context.setElectionStart(false);
        context.setElectionStop(false);
        gson = new Gson();
        contextDao = new ContextDaoImpl();
    }

    public ContextService(Context context) {
        this.context = context;
        contextDao = new ContextDaoImpl(context);
    }

    public void sync() {
        contextDao.sync(context);
    }

    public Context getContext() {
        return context;
    }

    public boolean isElectionStart() {
        return context.getElectionStart();
    }

    public boolean isElectionStop() {
        return context.getElectionStop();
    }

    public String setIsElectionStart(String requestJsonString) {
        SetIsElectionStartDtoRequest request = gson.fromJson(requestJsonString, SetIsElectionStartDtoRequest.class);
        context.setElectionStart(request.isElectionStart());
        return gson.toJson(new SetIsElectionStartDtoResponse(context.getElectionStart()));
    }

    public String setIsElectionStop(String requestJsonString) {
        SetIsElectionStopDtoRequest request = gson.fromJson(requestJsonString, SetIsElectionStopDtoRequest.class);
        context.setElectionStop(request.isElectionStop());
        return gson.toJson(new SetIsElectionStopDtoResponse(context.getElectionStop()));
    }

}