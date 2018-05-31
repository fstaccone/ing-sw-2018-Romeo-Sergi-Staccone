package it.polimi.ingsw.socket;

import it.polimi.ingsw.socket.requests.*;
import it.polimi.ingsw.socket.responses.Response;

public interface RequestHandler {
    Response handle(CheckUsernameRequest request);

    Response handle(CreateMatchRequest request);

    Response handle(AddPlayerRequest request);

    Response handle(RemoveFromWaitingPlayersRequest request);

    Response handle(GoThroughRequest request);

    Response handle(ChooseWindowRequest request);

    Response handle(PlaceDiceRequest request);

    Response handle(UseToolCard1Request request);

    Response handle(UseToolCard2or3Request request);

    Response handle(UseToolCard4Request request);

    Response handle(UseToolCard5Request request);

    Response handle(UseToolCard6Request request);

    Response handle(UseToolCard7Request request);

    Response handle(UseToolCard8Request request);

    Response handle(UseToolCard9Request request);

    Response handle(UseToolCard10Request request);

    Response handle(UseToolCard12Request request);

    Response handle(QuitGameRequest request);

    Response handle(ReconnectionRequest request);

    Response handle(ShowPlayersRequest request);
}
