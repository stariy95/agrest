package io.agrest.runtime.processor.update;

import io.agrest.encoder.Encoder;
import io.agrest.processor.Processor;
import io.agrest.processor.ProcessorOutcome;
import io.agrest.runtime.encoder.IEncoderService;
import org.apache.cayenne.di.Inject;

/**
 * @since 4.8
 */
public class EncoderInstallStage implements Processor<UpdateContext<?>> {

    private final IEncoderService encoderService;

    public EncoderInstallStage(@Inject IEncoderService encoderService) {
        this.encoderService = encoderService;
    }

    @Override
    public ProcessorOutcome execute(UpdateContext<?> context) {

        // make sure we create the encoder, even when the result is empty, as we need to encode the totals
        if (context.isIncludingDataInResponse() && context.getEncoder() == null) {
            context.setEncoder(createEncoder(context));
        }

        return ProcessorOutcome.CONTINUE;
    }

    private <T> Encoder createEncoder(UpdateContext<T> context) {
        return encoderService.dataEncoder(context.getEntity(), context);
    }
}
