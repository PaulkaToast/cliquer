import React, { Component } from 'react'
import { Button } from 'reactstrap'
import { connect } from 'react-redux'

import '../css/SearchResults.css'
import { search } from '../redux/actions'
import url from '../server'
import nFlag from '../img/newUser.png'

class SearchResults extends Component {

  componentDidMount = () => {
    const { query, category } = this.props.match ? this.props.match.params : this.props
    if(query && category && this.props.token) {
      this.props.search(`${url}/api/search?userId=${this.props.accountID}&query=${query}&type=${category}`, { 'X-Authorization-Firebase': this.props.token})
    }
  }

  componentWillReceiveProps = (nextProps) => {
    const { query, category } = nextProps.match ? nextProps.match.params : nextProps
    const oldQuery = this.props.match ? this.props.match.params.query : this.props.query
    if(category && oldQuery !== query && nextProps.token) {
      this.props.search(`${url}/api/search?userId=${nextProps.accountID}&query=${query}&type=${category}`, { 'X-Authorization-Firebase': nextProps.token})
    }
  }

  renderGroupPreview = (group, i) => {
    return (
      <div className="search-result" key={group.groupID}>
        <div className="right-content">
          <div className="right-top-content">
            <div className="search-name">{group.groupName.length > window.innerWidth/75 ? (group.groupName.substring(0, window.innerWidth/75) + '...') : group.groupName}</div>
            <div className="search-reputation">{}</div>
          </div>
          <div className="right-bottom-content">
            <div className="search-bio">{group.groupPurpose ? (group.groupPurpose.length > window.innerWidth/25 ? (group.groupPurpose.substring(0, window.innerWidth/25) + '...') : group.groupPurpose) : 'This group has no purpose.'}</div>
          </div>
        </div>
        <Button color="success" className="friend-request" onClick={() => this.props.requestToJoin(group.groupID, group.groupLeaderID)}>Request to Join</Button>
      </div>
    )
  }

  renderUserPreview = (user, i) => {
    let flag = user.newUser ? nFlag : "";
    return (
      <div className="search-result" onClick={(ev) => this.props.goToProfile(ev, user.accountID, document.querySelector('.friend-request'))} key={user.accountID}>
        <div className="right-content">
          <div className="right-top-content">
            <div className="search-name">{user.fullName != null ? (user.fullName.length > window.innerWidth/50 ? (user.fullName.substring(0, window.innerWidth/50) + '...') : user.fullName) : user.fullName}<img className="profile-user-flag" src={flag} alt=""></img></div>
            <div className="search-reputation">{user.reputation}</div>
          </div>
        </div>
        <Button color="success" className="friend-request" onClick={() => this.props.sendFriendRequest(user.accountID)}>Send Friend Request</Button>
      </div>
    )
  }

  renderResults = (renderResult) => {
    const { results } = this.props
    if(results) {
      return (
        <div className="list-container">
          <ul className="results-list">
            {Object.keys(results).map((key, i) => {
              if(i % 2 === 0) {
               return renderResult(results[key], i)
              }
            })}
          </ul>
          <ul className="results-list">
            {Object.keys(results).map((key, i) => {
              if(i % 2 === 1) {
                return renderResult(results[key], i)
              }
            })}
          </ul>
        </div>
      )
    }
  }

  render() {
    const { category } = this.props.match ? this.props.match.params : this.props
    return (
      <div className="SearchResults">
        { category === 'group' || category === 'isPublic'
          ? this.renderResults(this.renderGroupPreview)
          : this.renderResults(this.renderUserPreview)
        }
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    token: state.auth.token,
    results: state.search && state.search.data ? state.search.data : null,
    accountID: state.user.accountID,
	}
}

const mapDispatchToProps = (dispatch) => {
  return {
    search: (url, headers) => dispatch(search(url, headers))
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(SearchResults)
