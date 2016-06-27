### Result Type

A simple result type. Allowing you to do stuff like this:

```java

import com.mattmcnew.result.Result;
import java.util.Optional;

public class SomeController {

    Service service = new Service();

    public String someEndpoint(Object handleMe) {

        Result<Subscription, Exception> result = service.handle(handleMe);

        String log = result
                .mapFailure(ErrorResponse::new)
                .when()
                .success(s -> Result.success("Hooray", String.class))
                .failure(f -> Result.failure("Blah"))
                .when()
                .failure(String::toString)
                .success(String::toString);

        System.out.println(log);


        return result
                .mapFailure(ErrorResponse::new)
                .when()
                .failure(error -> "Apparently: " + error.getExplanation())
                .success(Subscription::getId);
    }

}

class Service {

    Result<Subscription, Exception> handle(Object handleMe) {
        Optional<Subscription> optional = someCallThatReturnsAnOptional();

        return optional
                .map(subscription -> Result.success(subscription, Exception.class))
                .orElse(Result.failure(new CustomException()));
    }
}


class Subscription {
    public String getId() {
        return "Id";
    }
}


class ErrorResponse {
    private Exception cause;

    public ErrorResponse(Exception cause) {
        this.cause = cause;
    }

    public String getExplanation() {
        return cause.getMessage();
    }
}

```