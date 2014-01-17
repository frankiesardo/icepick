package icepick.processor;

import java.util.Arrays;

import javax.annotation.processing.Processor;

final class ProcessorTestUtilities {
  static Iterable<? extends Processor> icepickProcessors() {
    return Arrays.asList(
        new IcepickProcessor()
    );
  }
}