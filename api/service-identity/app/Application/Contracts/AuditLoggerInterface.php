<?php

namespace App\Application\Contracts;

use App\Domain\Audit\Enums\AuditAction;

interface AuditLoggerInterface
{
    public function log(AuditAction $action, ?int $userId = null): void;
}
