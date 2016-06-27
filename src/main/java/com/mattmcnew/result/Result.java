package com.mattmcnew.result;


import java.util.function.Function;

public abstract class Result<S, F> {

    public interface Cases<S, F, R> {

        R success(S success);

        R failure(F success);

    }

    public abstract <R> Result<R, F> flatMapSuccess(Function<S, Result<R, F>> successMapper);

    public abstract <R> Result<S, R> flatMapFailure(Function<F, Result<S, R>> failureMapper);

    public abstract <R> Result<R, F> mapSuccess(Function<S, R> successMapper);

    public abstract <R> Result<S, R> mapFailure(Function<F, R> failureMapper);

    public abstract <R> R match(Cases<S, F, R> cases);

    public static <S, F> Result<S, F> failure(F failure) {
        return new Result<S, F>() {

            @Override
            public <R> Result<R, F> flatMapSuccess(Function<S, Result<R, F>> successMapper) {
                return Result.failure(failure);
            }

            @Override
            public <R> Result<S, R> flatMapFailure(Function<F, Result<S, R>> failureMapper) {
                return failureMapper.apply(failure);
            }

            @Override
            public <R> Result<R, F> mapSuccess(Function<S, R> successMapper) {
                return Result.failure(failure);
            }

            @Override
            public <R> Result<S, R> mapFailure(Function<F, R> failureMapper) {
                return Result.failure(failureMapper.apply(failure));
            }

            @Override
            public <R> R match(Cases<S, F, R> cases) {
                return cases.failure(failure);
            }
        };
    }

    //Satisfy Java Type System
    public static <S, F> Result<S, F> success(S success, Class<F> failureClass) {
        return success(success);
    }

    public static <S, F> Result<S, F> success(S success) {
        return new Result<S, F>() {

            @Override
            public <R> Result<R, F> flatMapSuccess(Function<S, Result<R, F>> successMapper) {
                return successMapper.apply(success);
            }

            @Override
            public <R> Result<S, R> flatMapFailure(Function<F, Result<S, R>> failureMapper) {
                return Result.success(success);
            }

            @Override
            public <R> Result<R, F> mapSuccess(Function<S, R> successMapper) {
                return Result.success(successMapper.apply(success));
            }

            @Override
            public <R> Result<S, R> mapFailure(Function<F, R> failureMapper) {
                return Result.success(success);
            }

            @Override
            public <R> R match(Cases<S, F, R> cases) {
                return cases.success(success);
            }
        };
    }

    public static <S, F, R> Cases<S, F, R> cases(final Function<? super S, R> successCase, final Function<? super F, R> failureCase) {
        return new Cases<S, F, R>() {
            @Override
            public R success(S success) {
                return successCase.apply(success);
            }

            @Override
            public R failure(F failure) {
                return failureCase.apply(failure);
            }
        };
    }

    public WhenBuilder when() {
        return new WhenBuilder();
    }

    public class WhenBuilder {
        <R> WhenSuccessBuilder<R> success(final Function<? super S, R> successMapper) {
            return new WhenSuccessBuilder<>(successMapper);
        }

        <R> WhenFailureBuilder<R> failure(final Function<? super F, R> failureMapper) {
            return new WhenFailureBuilder<>(failureMapper);
        }

    }

    public class WhenSuccessBuilder<R> {
        private Function<? super S, R> successMapper;

        public WhenSuccessBuilder(Function<? super S, R> successMapper) {
            this.successMapper = successMapper;
        }

        R failure(final Function<? super F, R> failureMapper) {
            return match(cases(successMapper, failureMapper));
        }
    }

    public class WhenFailureBuilder<R> {
        private Function<? super F, R> failureMapper;

        public WhenFailureBuilder(Function<? super F, R> failureMapper) {
            this.failureMapper = failureMapper;
        }

        R success(final Function<? super S, R> successMapper) {
            return match(cases(successMapper, failureMapper));
        }
    }

}