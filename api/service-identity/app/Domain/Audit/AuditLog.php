<?php

namespace App\Domain\Audit;

use App\Domain\Audit\Enums\AuditAction;
use DateTimeImmutable;

class AuditLog
{
    public function __construct(
        private readonly string $id,
        private readonly AuditAction $action,
        private readonly ?int $userId,
        private readonly ?string $ipAddress,
        private readonly ?string $userAgent,
        private readonly DateTimeImmutable $createdAt
    ) {}

    public function getId(): string
    {
        return $this->id;
    }

    public function getAction(): AuditAction
    {
        return $this->action;
    }

    public function getUserId(): ?int
    {
        return $this->userId;
    }

    public function getIpAddress(): ?string
    {
        return $this->ipAddress;
    }

    public function getUserAgent(): ?string
    {
        return $this->userAgent;
    }

    public function getCreatedAt(): DateTimeImmutable
    {
        return $this->createdAt;
    }
}
