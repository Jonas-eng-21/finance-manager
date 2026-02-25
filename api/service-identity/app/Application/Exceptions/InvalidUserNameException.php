<?php

namespace App\Domain\User\Exceptions;

use Exception;

class InvalidUserNameException extends Exception
{
    public function __construct(string $message = 'identity.user.errors.invalid_name')
    {
        parent::__construct($message);
    }
}
