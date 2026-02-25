<?php

namespace App\Application\Exceptions;

use Exception;

class EmailAlreadyExistsException extends Exception
{
    public function __construct(string $message = 'identity.user.errors.email_already_exists')
    {
        parent::__construct($message);
    }
}
