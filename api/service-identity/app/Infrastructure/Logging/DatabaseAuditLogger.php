<?php

namespace App\Infrastructure\Logging;

use App\Application\Contracts\AuditLoggerInterface;
use App\Domain\Audit\Enums\AuditAction;
use App\Domain\Audit\AuditLog;
use App\Domain\Audit\AuditLogRepositoryInterface;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use DateTimeImmutable;

class DatabaseAuditLogger implements AuditLoggerInterface
{
    public function __construct(
        private readonly AuditLogRepositoryInterface $repository,
        private readonly Request $request
    ) {}

    public function log(AuditAction $action, ?int $userId = null): void
    {
        $auditLog = new AuditLog(
            id: Str::uuid()->toString(),
            action: $action,
            userId: $userId,
            ipAddress: $this->request->ip(),
            userAgent: $this->request->userAgent(),
            createdAt: new DateTimeImmutable()
        );

        $this->repository->save($auditLog);
    }
}
