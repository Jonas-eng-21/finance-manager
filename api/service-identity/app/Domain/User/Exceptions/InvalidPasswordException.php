<?php

namespace App\Domain\User\Exceptions;

use Exception;

class InvalidPasswordException extends Exception
{
    public function __construct(string $message = 'identity.user.errors.invalid_password')
    {
        parent::__construct($message);
    }
}
