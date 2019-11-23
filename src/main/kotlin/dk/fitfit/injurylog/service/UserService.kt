package dk.fitfit.injurylog.service

import dk.fitfit.injurylog.domain.User

interface UserService {
    fun save(user: User): User
    fun findByEmail(email: String): User?
    fun findAll(): Iterable<User>
}
