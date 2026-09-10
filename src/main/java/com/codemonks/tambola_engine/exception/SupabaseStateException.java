    package com.codemonks.tambola_engine.exception;



    import com.codemonks.tambola_engine.enums.TambolaErrorCodesEnum;
    import lombok.Getter;

    @Getter
    public class SupabaseStateException extends RuntimeException {

        private final TambolaErrorCodesEnum errorCode = TambolaErrorCodesEnum.SUPABASE_STATE_ERROR;

        public SupabaseStateException(String message) {
            super(message);
        }

        public SupabaseStateException(String message, Throwable cause) {
            super(message, cause);
        }
    }