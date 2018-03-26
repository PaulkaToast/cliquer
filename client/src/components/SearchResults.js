import React, { Component } from 'react'
import { Button } from 'reactstrap'
import { connect } from 'react-redux'

import '../css/SearchResults.css'
import { search } from '../redux/actions'
import url from '../server'

class SearchResults extends Component {

  componentWillReceiveProps = (nextProps) => {
    const { query, category } = nextProps.match.params

    console.log(query, category)
    if(query && category && nextProps.token && !nextProps.results) {
      console.log('here 2')
      this.props.search(`${url}/api/search?query=${query}&type=${category}`, { 'X-Authorization-Firebase': nextProps.token})
    }
  }

  renderGroupPreview = (group, i) => {

  }

  renderUserPreview = (user, i) => {
    //may need to change button function to be a prop later (to allow reusing for group member searching)
    return (
      <div className="user-result" onClick={(ev) => this.props.goToProfile(ev, user.accountID, document.querySelector('.friend-request'))} key={user.accountID}>
        <div className="right-content">
          <div className="right-top-content">
            <div className="user-name">{user.fullName}</div>
            <div className="user-reputation">{user.reputation}</div>
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
    const { category } = this.props.match.params
    return (
      <div className="SearchResults">
        { category === 'group'
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
	}
}

const mapDispatchToProps = (dispatch) => {
  return {
    search: (url, headers) => dispatch(search(url, headers))
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(SearchResults)