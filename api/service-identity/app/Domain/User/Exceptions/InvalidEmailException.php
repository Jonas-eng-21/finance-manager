<?php

namespace App\Domain\User\Exceptions;

use Exception;

class InvalidEmailException extends Exception
{
    public function __construct(string $message = 'identity.user.errors.invalid_email')
    {
        parent::__construct($message);
    }
}
