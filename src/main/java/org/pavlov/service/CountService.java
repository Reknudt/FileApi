package org.pavlov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;


@RequiredArgsConstructor
@Service
public class CountService {

    public Long countCharsInFileByDivide(MultipartFile file) {

        AtomicLong byteCount = new AtomicLong(0);

        long fileSize = file.getSize();

        long chunkSize = fileSize / 10;

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            final long start = i * chunkSize;
            final long end = (i == 9) ? fileSize : (i + 1) * chunkSize;

            executor.submit(() -> {
                long count = end - start;
                byteCount.addAndGet(count);
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {}

        return byteCount.get();
    }
}