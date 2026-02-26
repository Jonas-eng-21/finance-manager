<?php

namespace App\Domain\User\Events;

class UserDeletedEvent
{
    public function __construct(
        public readonly string $email
    ) {}
}
