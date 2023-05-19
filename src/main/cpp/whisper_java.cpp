#include "whisper.h"

struct whisper_full_params default_params; // = (struct whisper_full_params *)malloc(sizeof(struct whisper_full_params));

struct whisper_java_params {
};

struct whisper_java_params whisper_java_default_params(enum whisper_sampling_strategy strategy) {
    printf("calling whisper_full_default_params...");
    default_params = whisper_full_default_params(strategy);
    printf("default_params = %d", default_params);

    struct whisper_java_params result = {};
    return result;
}

/** Delegates to whisper_full, but without having to pass `whisper_java_params` */
int whisper_java_full(
          struct whisper_context * ctx,
      struct whisper_java_params   params,
                     const float * samples,
                             int   n_samples) {
    return whisper_full(ctx, default_params, samples, n_samples);
}

void whisper_java_free() {
//    free(default_params);
}
