<?php

namespace App\Infrastructure\Logging;

use Illuminate\Log\Logger;
use Monolog\Formatter\JsonFormatter;
use Monolog\LogRecord;
use Monolog\Processor\ProcessorInterface;

class CorrelationIdProcessor implements ProcessorInterface
{
    public function __invoke(LogRecord $record): LogRecord
    {
        $correlationId = app()->bound('correlation_id') ? app()->make('correlation_id') : 'CLI-Process';

        $record->extra['correlation_id'] = $correlationId;

        return $record;
    }
}

class JsonLogFormatter
{
    public function __invoke(Logger $logger): void
    {
        foreach ($logger->getHandlers() as $handler) {
            $handler->setFormatter(new JsonFormatter());
            $handler->pushProcessor(new CorrelationIdProcessor());
        }
    }
}
