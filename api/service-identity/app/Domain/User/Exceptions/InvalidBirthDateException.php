<?php

namespace App\Domain\User\Exceptions;

use Exception;

class InvalidBirthDateException extends Exception
{
    public function __construct(string $message = 'identity.user.errors.invalid_birth_date')
    {
        parent::__construct($message);
    }
}
